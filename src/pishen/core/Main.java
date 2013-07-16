package pishen.core;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import pishen.db.DBHandler;
import pishen.db.Record;

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
		
		for(Record record: Record.getAllRecords(dbHandler)){
			log.info("Check Record: " + record.getName());
			try {
				record.updateCitationType();
			} catch (IOException e) {
				log.error("error reading textRecord", e);
			}
		}
	}
}
