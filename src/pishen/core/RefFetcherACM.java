package pishen.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pishen.db.DBHandler;
import pishen.db.node.Record;
import pishen.db.node.RecordKey;
import pishen.db.node.Reference;
import pishen.db.rel.HasRef;
import pishen.exception.DownloadFailException;
import pishen.tool.Downloader;

public class RefFetcherACM {
	private static final Logger log = Logger.getLogger(RefFetcherACM.class);
	private Record record;
	private OutputStream outputBuffer;
	
	public RefFetcherACM(Record record){
		this.record = record;
	}
	
	public void fetchRef(){
		if(!record.hasProperty(RecordKey.HAS_REF) || record.getBooleanProperty(RecordKey.HAS_REF)){
			try {
				//TODO check if ref relationship exist
				downloadRefPage();
				parseRefPage();
			} catch (DownloadFailException e) {
				log.info("Fail downloading ref page");
			} catch (NullPointerException e) {
				log.error("NullPointerException on parsing html", e);
			} catch (IndexOutOfBoundsException e) {
				log.error("IndexOutOfBoundsException on parsing html", e);
			}
		}
	}
	
	private void downloadRefPage() throws DownloadFailException{
		URL refURL = null;
		try {
			refURL = new URL(record.getStringProperty(RecordKey.EE) + "&preflayout=flat");
		} catch (MalformedURLException e) {
			log.error("MalformedURLException: " + refURL, e);
			throw new DownloadFailException();
		}
		
		outputBuffer = new ByteArrayOutputStream();
		Downloader.downloadFileWithRetry(refURL, outputBuffer, "text/html");
	}
	
	private void parseRefPage(){
		Document doc = Jsoup.parse(outputBuffer.toString());
		
		//may throw NullPointerException, IndexOutOfBoundsException
		Element refHeading = doc.getElementsByAttributeValue("name", "references").first();
		if(refHeading == null){
			record.setProperty(RecordKey.HAS_REF, false);
			log.info("no reference found");
		}else{
			Element table = refHeading.parent().nextElementSibling().getElementsByTag("table").first();
			if(table == null){
				record.setProperty(RecordKey.HAS_REF, false);
				log.info("no reference found");
			}else{
				record.setProperty(RecordKey.HAS_REF, true);
				parseTable(table);
			}
		}
	}
	
	private void parseTable(Element table){
		//may throw NullPointerException, IndexOutOfBoundsException
		Elements rows = table.getElementsByTag("tr");
		int count = 0;
		for(Element row: rows){
			count++;
			Element cellDiv = row.child(2).child(0);
			
			Reference ref = DBHandler.createReference();
			HasRef hasRef = record.createHasRefTo(ref);
			
			hasRef.setProperty(HasRef.CITATION_MARK, count);
			ref.setProperty(Reference.CONTENT, cellDiv.text());
			
			//write the links into Reference as String[]
			Elements links = cellDiv.select("a");
			String[] linkStrings = new String[links.size()];
			for(int i = 0; i < links.size(); i++){
				linkStrings[i] = links.get(i).attr("href");
			}
			ref.setProperty(Reference.LINKS, linkStrings);
			
		}
	}
}
