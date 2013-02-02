package pishen.core;

import java.net.MalformedURLException;
import java.net.URL;

import pishen.db.DBRecord;
import pishen.exception.URLNotFoundException;

public class URLGenerator {
	
	public static URL generatePDFURL(DBRecord dbRecord) throws MalformedURLException, URLNotFoundException{
		String eeStr = dbRecord.getStringProperty(Key.EE);
		URL eeURL = new URL(eeStr);
		
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = eeStr.substring(eeStr.lastIndexOf(".") + 1);
			return new URL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID);
		}else{
			throw new URLNotFoundException();
		}
	}
}
