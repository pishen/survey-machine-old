package pishen.core;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pishen.db.DBRecord;
import pishen.exception.DownloadFailException;
import pishen.tool.Downloader;

public class RefFetcherACM {
	private static final Logger log = Logger.getLogger(RefFetcherACM.class);
	private DBRecord dbRecord;
	private OutputStream outputBuffer;
	
	public RefFetcherACM(DBRecord dbRecord){
		this.dbRecord = dbRecord;
	}
	
	public void fetchRef(){
		if(!dbRecord.hasProperty(RecordKey.HAS_REF) || dbRecord.getBooleanProperty(RecordKey.HAS_REF)){
			try {
				//TODO check if ref file exist
				downloadRefPage();
				parseRefPage();
			} catch (DownloadFailException e) {
				log.info("Fail downloading ref page");
			} catch (NullPointerException e) {
				log.error("NullPointerException on parsing html");
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				log.error("IndexOutOfBoundsException on parsing html");
				e.printStackTrace();
			}
		}
	}
	
	private void downloadRefPage() throws DownloadFailException{
		URL refURL = null;
		try {
			refURL = new URL(dbRecord.getStringProperty(RecordKey.EE) + "&preflayout=flat");
		} catch (MalformedURLException e) {
			log.error("MalformedURLException: " + refURL);
			e.printStackTrace();
			throw new DownloadFailException();
		}
		
		outputBuffer = new ByteArrayOutputStream();
		Downloader.downloadFileWithRetry(refURL, outputBuffer, "text/html");
	}
	
	private void parseRefPage(){
		Document doc = Jsoup.parse(outputBuffer.toString());
		
		//may throw NullPointerException, IndexOutOfBoundsException
		Element refMarkDiv = doc.getElementsByAttributeValue("name", "references")
				.first()
				.parent()
				.nextElementSibling();
		Element table = refMarkDiv.getElementsByTag("table").first();
		if(table == null){
			dbRecord.setProperty(RecordKey.HAS_REF, false);
			log.info("no reference found");
		}else{
			dbRecord.setProperty(RecordKey.HAS_REF, true);
			/*
			try {
				parseTable(table);
			} catch (IOException e) {
				log.error("IOException on open/close ref output file");
				e.printStackTrace();
			}*/
		}
	}
	/*
	private void parseTable(Element table) throws IOException{
		PrintWriter out = new PrintWriter(new FileWriter(dbRecord.getRefFile()));
		
		try {
			//may throw NullPointerException, IndexOutOfBoundsException
			Elements rows = table.getElementsByTag("tr");
			for(Element row: rows){
				Element cellDiv = row.child(2).child(0);
				//TODO doi checking method is wrong
				
				
				if(cellDiv.ownText().contains("[doi>")){
					String href = cellDiv.select("a").last().attr("href");
					String id = href.substring(href.lastIndexOf(".") + 1);
					//log.info("[ID]" + id);
					out.println("[ID]" + id);
				}else{
					//log.info("[REF]" + cellDiv.text());
					out.println("[REF]" + cellDiv.text());
				}
			}
		} finally {
			out.close();
		}
		
	}*/
}
