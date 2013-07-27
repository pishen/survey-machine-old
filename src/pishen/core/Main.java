package pishen.core;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.neo4j.graphdb.Direction;

import pishen.db.DBHandler;
import pishen.db.Record;
import pishen.db.Reference;

import com.lexicalscope.jewel.cli.CliFactory;


public class Main {
	private static CLIOptions options;
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		//setting log4j
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n")));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(
					new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n"), "survey-machine.log", false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//setting options
		options = CliFactory.parseArguments(CLIOptions.class, args);
		
		//main process with RuntimeException catched
		try{
			mainWithCatch();
		}catch(RuntimeException e){
			log.error("RuntimeException", e);
		}
	}
	
	public static void mainWithCatch(){
		DBHandler dbHandler = new DBHandler("new-graph-db");
		
		Record sourceRecord = null;
		
		for(Record record: Record.getAllRecords(dbHandler)){
			log.info("Check Record " + record.getName());
			int count = 0;
			for(Reference ref: record.getReferences(Direction.OUTGOING)){
				Record targetRecord = ref.getEndRecord();
				if(targetRecord != null && targetRecord.getCitationType() == CitationMark.Type.NUMBER){
					count++;
				}
			}
			if(count == 157){
				sourceRecord = record;
				break;
			}
		}
		
		if(sourceRecord != null){
			List<TestCase> testCases = TestCase.createTestCaseList(sourceRecord, 0.1);
			double map = new MAPComputer(50).computeMAPOn(testCases);
			log.info("MAP=" + map);
		}else{
			log.error("source not found");
		}
	}
}
