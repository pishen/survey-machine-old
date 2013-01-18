package pishen.dblp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	public void linkRecords() throws XMLStreamException, IOException{
		XMLParser xmlParser = new XMLParser(XML_FILENAME);
		//int paperWithReference = 0;
		boolean found = false;
		
		while(xmlParser.hasNextXMLRecord() && !found){
			XMLRecord xmlRecord = xmlParser.getNextXMLRecord();
			File textRecord = EEHandler.getTextRecord(xmlRecord.getProperty(Key.FILENAME).toString());
			log.info("parsing name:" + textRecord.getName());
			if(textRecord.getName().equals("journals-toct-BeameIPS10")){
				log.info("file found");
				BufferedReader in = new BufferedReader(new FileReader(textRecord));
				BufferedWriter out = new BufferedWriter(new FileWriter("output"));
				String line = null;
				try {
					log.info("writing");
					while((line = in.readLine()) != null){
						out.write(line);
						out.newLine();
					}
					in.close();
					out.close();
					log.info("done");
				} catch (IOException e) {
					log.error("error on reading textrecord:" + xmlRecord.getProperty(Key.FILENAME));
				}
			}else{
				log.error("text record not exist");
			}
		}
		
		//log.info("paperWithReference=" + paperWithReference);
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
