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
import org.neo4j.graphdb.Transaction;

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
		//HAS_REF==true: all references are parsed and added
		//HAS_REF==false: no reference available
		//TODO fixing has_ref=true && has_ref_count=0 records
		if(!record.hasProperty(RecordKey.HAS_REF)){
			try {
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
			log.info("unknown item");
			record.setProperty(RecordKey.HAS_REF, false);
		}else{
			Element table = refHeading.parent().nextElementSibling().getElementsByTag("table").first();
			if(table == null){
				log.info("no reference available");
				record.setProperty(RecordKey.HAS_REF, false);
			}else{
				//make the whole section atomic
				Transaction tx = DBHandler.getTransaction();
				try {
					parseTable(table);
					record.setProperty(RecordKey.HAS_REF, true);
					log.info("finish adding references");
					tx.success();
				} finally {
					tx.finish();
				}
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
			for(int j = 0; j < links.size(); j++){
				linkStrings[j] = links.get(j).attr("href");
			}
			ref.setProperty(Reference.LINKS, linkStrings);
		}
	}

}
