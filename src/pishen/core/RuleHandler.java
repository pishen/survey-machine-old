package pishen.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import pishen.db.Record;
import pishen.exception.RuleNotFoundException;

public class RuleHandler {
	private static final Logger log = Logger.getLogger(RuleHandler.class);
	
	public static URL getPDFURL(Record record) throws RuleNotFoundException{
		URL eeURL = record.getEE();
		
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = eeURL.toString().substring(eeURL.toString().lastIndexOf(".") + 1);
			try {
				return new URL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID);
			} catch (MalformedURLException e) {
				log.error("PDF URL error", e);
				throw new RuleNotFoundException();
			}
		}else{
			throw new RuleNotFoundException();
		}
	}
	
	public static RefFetcherACM getRefFetcher(Record record) throws RuleNotFoundException{
		//TODO fix
		return null;
		/*URL eeURL = record.getEE();
		
		if(eeURL.getHost().equals("doi.acm.org")){
			return new RefFetcherACM(record);
		}else{
			throw new RuleNotFoundException();
		}*/
	}
	
	public static RecordConnectorACM getRecordConnector(Record record) throws RuleNotFoundException{
		//TODO fix
		return null;
		/*URL eeURL = record.getEE();
		
		if(eeURL.getHost().equals("doi.acm.org")){
			return new RecordConnectorACM(record);
		}else{
			throw new RuleNotFoundException();
		}*/
	}
}
