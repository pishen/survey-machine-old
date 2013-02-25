package pishen.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.apache.log4j.Logger;

import pishen.db.node.Record;
import pishen.db.node.RecordKey;
import pishen.exception.DownloadFailException;
import pishen.tool.Downloader;
import pishen.tool.Executor;

public class ContentFetcher {
	private static final Logger log = Logger.getLogger(ContentFetcher.class);

	//TODO add info to Record to avoid fetching paper with wrong content-type
	public static void fetchContent(Record record){
		if(record.getTextFile().exists() == false){
			try {
				downloadPDF(record);
				checkEmbeddedFonts(record);
				pdfToText(record);
			} catch (DownloadFailException e) {
				//throw new Exceptions if necessary
			} catch (IOException e) {
				log.error("error on open/close PDF file", e);
			} finally {
				//clean up unnecessary files
				record.getPDFFile().delete();
			}
		}
	}

	private static void downloadPDF(Record record) throws DownloadFailException, IOException{		
		File pdfFile = record.getPDFFile();
		
		if(pdfFile.exists()){
			log.info("PDF file exists");
			return;
		}else{
			log.info("downloading PDF");
			FileOutputStream out = new FileOutputStream(pdfFile);
			try {
				URL targetURL = RuleHandler.getPDFURL(record);
				Downloader.downloadFileWithRetry(targetURL, out, "application/pdf");
			} catch (Exception e) {
				throw new DownloadFailException();
			} finally {
				out.close();
			}
		}
	}
	
	private static void checkEmbeddedFonts(Record record){
		String cmdLineStr = "pdffonts " + record.getPDFFile().getPath();
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
			record.setProperty(RecordKey.EMB, true);
		}else if(token.equals("no")){
			record.setProperty(RecordKey.EMB, false);
		}else{
			//throw Exception if necessary
			return;
		}
	}
	
	private static void pdfToText(Record record){
		File pdfFile = record.getPDFFile();
		File textFile = record.getTextFile();
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
