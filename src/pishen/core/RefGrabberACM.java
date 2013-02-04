package pishen.core;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pishen.db.DBRecord;

public class RefGrabberACM {
	private static final Logger log = Logger.getLogger(RefGrabberACM.class);
	private DBRecord dbRecord;
	
	public RefGrabberACM(DBRecord dbRecord){
		this.dbRecord = dbRecord;
	}
	
	public void grabRef() throws IOException{
		String refStr = dbRecord.getStringProperty(Key.EE) + "&preflayout=flat";
		//Document doc = Jsoup.connect(refStr).get();
		//TODO use Downloader instead for retry
		
	}
}
