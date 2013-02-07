package pishen.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pishen.db.DBRecord;
import pishen.exception.DownloadFailException;
import pishen.tool.Downloader;

public class RefGrabberACM {
	private static final Logger log = Logger.getLogger(RefGrabberACM.class);
	private DBRecord dbRecord;
	
	public RefGrabberACM(DBRecord dbRecord){
		this.dbRecord = dbRecord;
	}
	
	public void grabRef() throws DownloadFailException, IOException{
		URL refURL = new URL(dbRecord.getStringProperty(Key.EE) + "&preflayout=flat");
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		
		Downloader.downloadFileWithRetry(refURL, outputBuffer, "text/html");
		
		Document doc = Jsoup.parse(outputBuffer.toString());
		
		//TODO catch NullPointerException, IndexOutOfBoundsException
		Element refMarkDiv = doc.getElementsByAttributeValue("name", "references")
				.first()
				.parent()
				.nextElementSibling();
		Element table = refMarkDiv.getElementsByTag("table").first();
		if(table == null){
			//TODO check
			log.info("no reference found");
			return;
		}else{
			//PrintWriter writer = new PrintWriter(new FileWriter(dbRecord.getRefFile()));
			
			Elements rows = table.getElementsByTag("tr");
			for(Element row: rows){
				Element cellDiv = row.child(2).child(0);
				if(cellDiv.ownText().contains("[doi>")){
					String href = cellDiv.select("a").last().attr("href");
					String id = href.substring(href.lastIndexOf(".") + 1);
					log.info("[ID] " + id);
				}else{
					log.info("[REF] " + cellDiv.text());
				}
			}
		}
	}
}
