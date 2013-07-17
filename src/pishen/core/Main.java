package pishen.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

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
		
		TreeMap<Integer, Integer> degreeCountMap = new TreeMap<Integer, Integer>(new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.intValue() - o1.intValue();
			}
		});
		
		for(Record record: Record.getAllRecords(dbHandler)){
			log.info("Check Record: " + record.getName());
			int refCount = 0;
			for(Reference ref: record.getReferences(Direction.OUTGOING)){
				if(ref.getEndRecord() != null){
					refCount++;
				}
			}
			if(degreeCountMap.containsKey(refCount)){
				degreeCountMap.put(refCount, degreeCountMap.get(refCount).intValue() + 1);
			}else{
				degreeCountMap.put(refCount, 1);
			}
		}
		
		for(Integer refCount: degreeCountMap.navigableKeySet()){
			log.info("refCount: " + refCount + " recordCount: " + degreeCountMap.get(refCount));
		}
	}
}
