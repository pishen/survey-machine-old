package pishen.dblp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import pishen.exception.DownloadFailException;

public class Controller {
	private static final Logger log = Logger.getLogger(Controller.class);
	private final String XML_FILENAME = "dblp.xml";
	
	public void startGraphDB(){
		DBHandler.startGraphDB();
	}
	
	public void downloadRecords() throws FileNotFoundException, XMLStreamException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			//copy the key-value pairs from XMLRecord to DBRecord
			DBRecord dbRecord = DBHandler.getRecordWithKey(xmlRecord.getRecordKey());
			for(Key key: Key.values()){
				dbRecord.setProperty(key, xmlRecord.getProperty(key));
			}
			//try to download EE
			tryDownloadRecord(dbRecord);
		}
	}
	
	public void linkRecords() throws FileNotFoundException, XMLStreamException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		int paperWithReference = 0;
		
		while(xmlParser.hasNextXMLRecord()){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			File textRecord = EEHandler.getTextRecord(xmlRecord.getProperty(Key.FILENAME).toString());
			if(textRecord.exists()){
				BufferedReader in = new BufferedReader(new FileReader(textRecord));
				String line = null;
				try {
					while((line = in.readLine()) != null){
						if(line.trim().equals("REFERENCES")){
							paperWithReference++;
							break;
						}
					}
				} catch (IOException e) {
					log.error("error on reading textrecord:" + xmlRecord.getProperty(Key.FILENAME));
				}
			}
		}
		
		log.info("paperWithReference=" + paperWithReference);
	}
	
	private void tryDownloadRecord(DBRecord dbRecord){
		try {
			EEHandler.downloadRecord(dbRecord);
			log.info("===SUCCESS===");
		} catch (DownloadFailException e) {
			//System.out.println("download fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO feature require: updating property value by XMLParser and delete the record that's not exist anymore 
	
}
