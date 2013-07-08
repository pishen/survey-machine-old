package pishen.core;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.neo4j.graphdb.Node;

import pishen.db.DBHandler;
import pishen.db.HasRef;
import pishen.db.Record;
import pishen.db.Reference;

import com.lexicalscope.jewel.cli.CliFactory;


public class Main {
	private static CLIOptions options;
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n")));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(
					new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n"), "survey-machine.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		options = CliFactory.parseArguments(CLIOptions.class, args);
		
		try{
			mainWithCatch();
		}catch(RuntimeException e){
			log.error("RuntimeException", e);
		}
	}
	
	public static void mainWithCatch(){
		DBHandler oldDB = new DBHandler("graph-db");
		DBHandler newDB = new DBHandler("new-graph-db");
		
		//rebuild the graph
		for(Node node: oldDB.getAllNodes()){
			if(node.getProperty("TYPE").equals("RECORD")){
				Record oldRecord = new Record(node, oldDB);
				log.info("Check Record: " + oldRecord.getName());
				if(oldRecord.getName().equals("journals-toit-TsoiHS06")){
					log.debug("skip");
					continue;
				}
				Record newRecord = newDB.getOrCreateRecord(oldRecord.getName());
				
				if(!node.hasProperty("EMB")){
					log.info("Fill default EMB as false");
					newRecord.setEmb(false);
				}
				
				for(HasRef hasRef: oldRecord.getHasRefs()){
					log.info("create and link Reference " + hasRef.getCitation());
					Reference oldReference = hasRef.getReference();
					Reference newReference = newDB.createReference();
					newReference.setIndex(Integer.parseInt(hasRef.getCitation()));
					newReference.setContent(oldReference.getContent());
					newReference.setLinks(oldReference.getLinks());
					newRecord.createRefTo(newReference, newReference.getIndex());
				}
			}
		}
		
		/*
		Options options = new Options();
		options.addOption("c", false, "fetch paper content");
		options.addOption("r", true, "fetch paper ref");
		options.addOption("l", false, "link the citation network");
		options.addOption("t", false, "testing");
		options.addOption("e", false, "evaluation");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			log.error("ParseException", e);
			return;
		}
		
		Controller.startGraphDB();
		
		try {
			if(cmd.hasOption("c")){
				//Controller.downloadRecords();
			}
			if(cmd.hasOption("r")){
				Controller.fetchRefForAllRecords(Integer.parseInt(cmd.getOptionValue("r")));
			}
			if(cmd.hasOption("l")){
				Controller.connectRecords();
			}
			if(cmd.hasOption("t")){
				Controller.test();
			}
			if(cmd.hasOption("e")){
				Controller.eval();
			}
		} catch(RuntimeException e) {
			log.error("Runtime error", e);
			System.exit(0);
		} catch(Exception e) {
			log.error("Exception:", e);
			System.exit(0);
		}*/
	}
}
