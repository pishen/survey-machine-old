package pishen.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.NotFoundException;

import pishen.db.DBRecord;
import pishen.exception.LinkingFailException;
import pishen.exception.NotEmbException;
import pishen.exception.TextRecordNotFoundException;

public class RecordLinker {
	private static final Logger log = Logger.getLogger(RecordLinker.class);
	
	private static int[] typeCounts = new int[8];
	private static DBRecord currentRecord;
	private static File textFile;
	
	private static PrintWriter checklistWriter;
	private static PrintWriter typeCountWriter;
	
	static{
		try {
			checklistWriter = new PrintWriter(new FileWriter("checklist"), true);
			typeCountWriter = new PrintWriter(new FileWriter("typecount"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeTypeCounts(){
		for(int i = 0; i < typeCounts.length; i++){
			typeCountWriter.println("type-" + (i + 1) + "=" + typeCounts[i]);
		}
	}
	
	public static void linkRecord(DBRecord dbRecord) throws LinkingFailException{
		RecordLinker.currentRecord = dbRecord;
		try {
			tryCheckingEMB();
			tryGettingTextRecordFile();
			tryReadingRecordContent();
		} catch (NotEmbException e) {
			throw new LinkingFailException();
		} catch (TextRecordNotFoundException e) {
			throw new LinkingFailException();
		} catch (IOException e) {
			log.error("IOException when reading record: " + dbRecord.getStringProperty(RecordKey.FILENAME));
			e.printStackTrace();
			throw new LinkingFailException();
		}
	}
	
	private static void tryCheckingEMB() throws NotEmbException{
		try {
			String emb = currentRecord.getStringProperty(RecordKey.EMB);
			if(!emb.equals("yes")){
				throw new NotEmbException();
			}
		} catch (NotFoundException e) {
			throw new NotEmbException();
		}
	}
	
	private static void tryGettingTextRecordFile() throws TextRecordNotFoundException{
		textFile = currentRecord.getTextFile();
		if(textFile.exists() == false){
			throw new TextRecordNotFoundException();
		}
	}
	
	private static void tryReadingRecordContent() throws IOException{
		//TODO check if this record has references
		
		String recordContent = readContentToSingleString();
		Pattern candidatePattern = Pattern.compile("\\[([^\\[\\]]*)\\]");
		Matcher matcher = candidatePattern.matcher(recordContent);
		
		boolean hasValidCitationMark = false;
		while(matcher.find()){
			if(validate(matcher.group(1))){
				hasValidCitationMark = true;
				break;
			}
		}
		
		if(!hasValidCitationMark){
			checklistWriter.println(currentRecord.getStringProperty(RecordKey.FILENAME));
		}
	}
	
	private static String readContentToSingleString() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(textFile));
		String recordContent = "", line = null;
		while((line = in.readLine()) != null){
			recordContent += line;
		}
		in.close();
		return recordContent;
	}
	
	private static boolean validate(String content){
		String noSpaceContent = content.replaceAll(" ", "");
		if(noSpaceContent.matches("[1-9]\\d{0,2}")){
			//[1] - [999]
			typeCounts[0]++;
			return true;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(,[1-9]\\d{0,2})+")){
			//[1,2,3]
			typeCounts[1]++;
			return true;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}-[1-9]\\d{0,2}")){
			//[1-10]
			typeCounts[2]++;
			return true;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(-[1-9]\\d{0,2})?(,[1-9]\\d{0,2}(-[1-9]\\d{0,2})?)+")){
			//[1,2-10,11,13]
			typeCounts[3]++;
			return true;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d")){
			//[1000] - [2999]
			typeCounts[4]++;
			return true;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d(;[12]\\d\\d\\d)+")){
			//[1988;2005;2013]
			typeCounts[5]++;
			return true;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d")){
			//[pishen tsai et al. 2013]
			typeCounts[6]++;
			return true;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d(;\\D+[12]\\d\\d\\d)+")){
			//[pishen tsai et al. 2013; pj cheng 2007]
			typeCounts[7]++;
			return true;
		}else{
			return false;
		}
	}
}
