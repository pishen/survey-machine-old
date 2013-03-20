package pishen.core;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] [%t] %m%n")));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(
					new PatternLayout("%d{MM-dd HH:mm:ss} [%p] [%t] %m%n"), "survey-machine.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Options options = new Options();
		options.addOption("c", false, "fetch paper content");
		options.addOption("r", true, "fetch paper ref");
		options.addOption("l", false, "link the citation network");
		options.addOption("t", false, "testing");
		
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
		} catch(RuntimeException e) {
			log.error("Runtime error", e);
			System.exit(0);
		} catch(Exception e) {
			log.error("Exception:", e);
			System.exit(0);
		}
	}
}
