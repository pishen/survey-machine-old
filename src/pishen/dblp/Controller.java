package pishen.dblp;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.exception.DownloadFailException;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private final String XML_FILENAME = "dblp.xml";
	private EEHandler eeHandler = new EEHandler();
	private DBHandler dbHandler = new DBHandler();
	
	public void start(){
		dbHandler.startGraphDB();
	}
	
	public void createRecords() throws FileNotFoundException, XMLStreamException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to DBRecord
			DBRecord dbRecord = dbHandler.getRecordWithKey(xmlRecord.getRecordKey());
			for(Key key: Key.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
			//try to download EE
			tryDownloadRecord(dbRecord);
		}
	}
	
	public void linkRecords(){
		
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
