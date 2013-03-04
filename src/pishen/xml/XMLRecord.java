package pishen.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import pishen.db.Record;

public class XMLRecord {
	private static final Logger log = Logger.getLogger(XMLRecord.class);
	private String name, title, year;
	private URL eeURL;
	
	protected XMLRecord(String name){
		this.name = name;
	}
	
	public void dumpTo(Record record){
		record.setTitle(title);
		record.setEE(eeURL);
		record.setYear(year);
	}
	
	public String getName(){
		return name;
	}
	
	protected void setEE(String ee){
		try {
			eeURL = new URL(ee);
		} catch (MalformedURLException e) {
			log.error("EE is not valid");
		}
	}
	
	protected void setTitle(String title){
		if((this.title = title) == null){
			log.error("title can't be null");
		}
	}
	
	protected void setYear(String year){
		try{
			Integer.parseInt(year);
		}catch(NumberFormatException e){
			log.error("year is not valid");
		}
		this.year = year;
	}
	
	protected boolean isValid(){
		if(title != null && year != null && eeURL != null && isTargetDomain()){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isTargetDomain(){
		String domainName = eeURL.getHost();
		
		//TODO add more domains
		if(domainName.equals("doi.acm.org")){
			return true;
		}else{
			return false;
		}
	}
}
