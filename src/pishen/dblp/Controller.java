package pishen.dblp;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.exception.DownloadFailException;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private XMLParser xmlParser = new XMLParser();
	private EEHandler eeHandler = new EEHandler();
	private DBHandler dbHandler = new DBHandler();
	
	public void start() throws FileNotFoundException, XMLStreamException{
		dbHandler.startGraphDB();
		
		xmlParser.setupReader("dblp.xml");
		
		log.info("parse through all the records in dblp.xml");
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			if(xmlRecord.isValid() && eeHandler.containsRuleForEE(xmlRecord.getProperty(Key.EE))){
				//copy the key-value pairs from XMLRecord to DBRecord
				DBRecord dbRecord = dbHandler.getRecordWithKey(xmlRecord.getRecordKey());
				for(Key key: Key.values()){
					dbRecord.setProperty(key, xmlRecord.getProperty(key));
				}
				//try to download EE
				tryDownloadRecord(dbRecord);
			}
		}
		
	}
	
	private void tryDownloadRecord(DBRecord dbRecord){
		try {
			eeHandler.downloadRecord(dbRecord);
			log.info("===SUCCESS===");
		} catch (DownloadFailException e) {
			//System.out.println("download fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
