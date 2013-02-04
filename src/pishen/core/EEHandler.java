package pishen.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.apache.log4j.Logger;

import pishen.db.DBRecord;
import pishen.exception.DownloadFailException;
import pishen.tool.Downloader;
import pishen.tool.Executor;

public class EEHandler {
	private static final Logger log = Logger.getLogger(EEHandler.class);

	public static void fetchResources(DBRecord dbRecord){
		if(dbRecord.getTextFile().exists() == false){
			try {
				downloadPDF(dbRecord);
				checkEmbeddedFonts(dbRecord);
				pdfToText(dbRecord);
				log.info("==files fetching SUCCESS==");
			} catch (DownloadFailException e) {
				//throw new Exceptions if necessary
			} finally {
				//clean up unnecessary files
				dbRecord.getPDFFile().delete();
			}
		}
		downloadRef(dbRecord);
		
	}

	private static void downloadPDF(DBRecord dbRecord) throws DownloadFailException{		
		File pdfFile = dbRecord.getPDFFile();
		
		if(pdfFile.exists()){
			log.info("PDF file exists");
			return;
		}else{
			log.info("downloading PDF");
			try {
				URL targetURL = RuleHandler.getPDFURL(dbRecord);
				Downloader.downloadFileWithRetry(targetURL, pdfFile, "application/pdf");
			} catch (Exception e) {
				pdfFile.delete();
				throw new DownloadFailException();
			}
		}
	}
	
	private static void downloadRef(DBRecord dbRecord){
		File refFile = dbRecord.getRefFile();
		
		if(refFile.exists()){
			log.info("Ref file exists");
			return;
		}else{
			log.info("downloading ref");
			//TODO
		}
	}
	
	private static void checkEmbeddedFonts(DBRecord dbRecord){
		String cmdLineStr = "pdffonts " + dbRecord.getPDFFile().getPath();
		ByteArrayOutputStream pdffontsOutput = new ByteArrayOutputStream();
		String line3 = "";
		
		try {
			//get output of pdffonts
			Executor.execWithTimeout(cmdLineStr, pdffontsOutput);
			
			//read the 3rd line of output of pdffonts into line3
			BufferedReader resultReader = new BufferedReader(new StringReader(pdffontsOutput.toString()));
			for(int i = 0; i < 3; i++){
				if((line3 = resultReader.readLine()) == null){
					//throw Exception if necessary
					return;
				}
			}
		} catch (IOException e) {
			//throw Exception if necessary
			return;
		}
		
		//get the "yes/no" part
		int tokenCount = 0;
		String token = "";
		for(String str: line3.split(" ")){
			if(!str.isEmpty()){
				token = str;
				tokenCount++;
				if(tokenCount == 4){
					break;
				}
			}
		}
		
		if(token.equals("yes")){
			dbRecord.setProperty(Key.EMB, "yes");
		}else if(token.equals("no")){
			dbRecord.setProperty(Key.EMB, "no");
		}else{
			//throw Exception if necessary
			return;
		}
	}
	
	private static void pdfToText(DBRecord dbRecord){
		File pdfFile = dbRecord.getPDFFile();
		File textFile = dbRecord.getTextFile();
		String cmdLineStr = "pdftotext " + pdfFile.getPath() + " " + textFile.getPath();
		
		try {
			Executor.execWithTimeout(cmdLineStr, System.out);
		} catch (IOException e) {
			textFile.delete();
			//throw Exception if necessary
			return;
		}
	}
	
}
