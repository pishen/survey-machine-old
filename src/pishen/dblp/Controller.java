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
		
		xmlParser.setupReader("dblp.xml", dbHandler);
		
		log.info("parse through all the records in dblp.xml");
		while(xmlParser.hasNextRecord()){
			Record record = xmlParser.getNextRecord();
			if(record.getProperty(Key.EE) != null){
				tryDownloadRecord(record);
			}
		}
		
	}
	
	private void tryDownloadRecord(Record record){
		try {
			eeHandler.downloadRecord(record);
			log.info("download success");
		} catch (DownloadFailException e) {
			//System.out.println("download fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO updating property value by XMLParser and delete the record that's not exist anymore 
	
}
