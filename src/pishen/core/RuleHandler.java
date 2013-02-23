package pishen.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import pishen.db.node.Record;
import pishen.db.node.RecordKey;
import pishen.exception.RuleNotFoundException;

public class RuleHandler {
	private static final Logger log = Logger.getLogger(RuleHandler.class);
	
	//TODO catch MalformedURLException inside
	public static URL getPDFURL(Record record) throws MalformedURLException, RuleNotFoundException{
		String eeStr = record.getStringProperty(RecordKey.EE);
		URL eeURL = new URL(eeStr);
		
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = eeStr.substring(eeStr.lastIndexOf(".") + 1);
			return new URL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID);
		}else{
			throw new RuleNotFoundException();
		}
	}
	
	public static RefFetcherACM getRefFetcher(Record record) throws RuleNotFoundException{
		String eeStr = record.getStringProperty(RecordKey.EE);
		URL eeURL = null;
		try {
			eeURL = new URL(eeStr);
		} catch (MalformedURLException e) {
			log.error("MalformedURLException: " + eeURL, e);
			throw new RuleNotFoundException();
		}
		
		if(eeURL.getHost().equals("doi.acm.org")){
			return new RefFetcherACM(record);
		}else{
			throw new RuleNotFoundException();
		}
	}
}
