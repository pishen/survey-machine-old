package pishen.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args){
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{MM-dd HH:mm:ss} [%p] %m%n")));
		Logger.getRootLogger().setLevel(Level.INFO);
		/*
		String html = "<p>This <a>is</a> a test.</p>";
		Document doc = Jsoup.parse(html);
		
		Element p = doc.getElementsByTag("p").first();
		log.info(p.getElementsContainingOwnText("This a test").size());
		*/
		
		Controller controller = new Controller();
		
		Options options = new Options();
		options.addOption("c", false, "fetch paper content");
		options.addOption("r", false, "fetch paper ref");
		options.addOption("l", false, "link the citation network");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			log.error("ParseException");
			e.printStackTrace();
			return;
		}
		
		if(cmd.hasOption("c")){
			//controller.downloadRecords();
		}
		
		if(cmd.hasOption("r")){
			controller.fetchRefForAllRecords();
		}
		
		if(cmd.hasOption("l")){
			//controller.linkRecords(Integer.parseInt(cmd.getOptionValue("l")));
			//controller.linkRecords();
		}
	}
}
