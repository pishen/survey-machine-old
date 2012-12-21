package pishen.dblp;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import pishen.exception.DownloadFailException;

public class Controller {
	public static Controller currentController;
	
	private XMLParser xmlParser = new XMLParser();
	private EEHandler eeHandler = new EEHandler();
	private DBHandler dbHandler = new DBHandler();
	
	public Controller(){
		currentController = this;
	}
	
	public void start() throws FileNotFoundException, XMLStreamException{
		dbHandler.startGraphDB();
		
		
		
		/*
		xmlParser.setupReader();
		while(xmlParser.parseForNextRecord() == true){
			handleRecord();
		}
		*/
	}
	
	private void handleRecord(){
		try {
			eeHandler.downloadRecord(xmlParser.getCurrentRecord());
			System.out.println("download success");
			//TODO add the record to graphDB
		} catch (DownloadFailException e) {
			//System.out.println("download fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
