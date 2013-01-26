package pishen.dblp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.NotFoundException;

import pishen.exception.LinkingFailException;
import pishen.exception.NotEmbException;
import pishen.exception.RefMarkNotFoundException;
import pishen.exception.TextRecordNotFoundException;

public class RecordLinker {
	private static final Logger log = Logger.getLogger(RecordLinker.class);
	private static int recordWithRefAndEmb = 0;
	private static File textRecordFile;
	private static DBRecord dbRecord;
	private static BufferedReader textRecordReader;
	private static String formatStr = "";
	private static BufferedWriter formatWriter;
	
	static{
		try {
			formatWriter = new BufferedWriter(new FileWriter("format"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void linkRecord(DBRecord dbRecord) throws LinkingFailException{
		RecordLinker.dbRecord = dbRecord;
		try {
			tryCheckingEMB();
			tryGettingTextRecordFile();
			tryReadingRecordContent();
		} catch (NotEmbException e) {
			throw new LinkingFailException();
		} catch (TextRecordNotFoundException e) {
			throw new LinkingFailException();
		} catch (RefMarkNotFoundException e) {
			throw new LinkingFailException();
		} catch (IOException e) {
			log.error("IOException when reading record: " + dbRecord.getStringProperty(Key.FILENAME));
			e.printStackTrace();
			throw new LinkingFailException();
		}
	}
	
	private static void tryCheckingEMB() throws NotEmbException{
		try {
			String emb = dbRecord.getStringProperty(Key.EMB);
			if(!emb.equals("yes")){
				throw new NotEmbException();
			}
		} catch (NotFoundException e) {
			throw new NotEmbException();
		}
	}
	
	private static void tryGettingTextRecordFile() throws TextRecordNotFoundException{
		textRecordFile = EEHandler.getTextRecordByFilename(dbRecord.getStringProperty(Key.FILENAME).toString());
	}
	
	private static void tryReadingRecordContent() throws IOException, RefMarkNotFoundException{
		textRecordReader = new BufferedReader(new FileReader(textRecordFile));
		
		tryFindingRefMark();
		checkRefFormat();
		
		textRecordReader.close();
	}
	
	private static void tryFindingRefMark() throws IOException, RefMarkNotFoundException{
		String line = null;
		while((line = textRecordReader.readLine()) != null){
			if(line.equals("REFERENCES") || line.equals("References")){
				recordWithRefAndEmb++;
				return;
			}
		}
		throw new RefMarkNotFoundException();
	}
	
	private static void checkRefFormat() throws IOException{
		String line = null;
		while((line = textRecordReader.readLine()) != null){
			if((line = line.trim()).length() > 0){
				break;
			}
		}
		
		String firstCharStr = line.substring(0, 1);
		if(!formatStr.contains(firstCharStr)){
			formatStr = formatStr + firstCharStr;
			formatWriter.write(firstCharStr + " " + dbRecord.getStringProperty(Key.FILENAME));
			formatWriter.newLine();
		}
	}
	
	public static int getRecordWithRefAndEmb(){
		return recordWithRefAndEmb;
	}
}
