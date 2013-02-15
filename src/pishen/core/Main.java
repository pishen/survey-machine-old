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
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n")));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n"), "logfile"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		String html = "<p>This <a>is</a> a test.</p>";
		Document doc = Jsoup.parse(html);
		
		Element p = doc.getElementsByTag("p").first();
		log.info(p.getElementsContainingOwnText("This a test").size());
		*/
		
		Options options = new Options();
		options.addOption("c", false, "fetch paper content");
		options.addOption("r", false, "fetch paper ref");
		options.addOption("l", false, "link the citation network");
		options.addOption("t", false, "testing");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			log.error("ParseException");
			e.printStackTrace();
			return;
		}
		
		Controller.startGraphDB();
		
		if(cmd.hasOption("c")){
			//Controller.downloadRecords();
		}
		
		if(cmd.hasOption("r")){
			//Controller.fetchRefForAllRecords();
		}
		
		if(cmd.hasOption("l")){
			//Controller.linkRecords(Integer.parseInt(cmd.getOptionValue("l")));
			//Controller.linkRecords();
		}
		
		if(cmd.hasOption("t")){
			Controller.test();
		}
	}
}
