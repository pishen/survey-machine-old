package pishen.core;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

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
		
		try {
			Options options = new Options();
			options.addOption("d", false, "download the papers");
			options.addOption("l", false, "link the citation network");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption("d")){
				//controller.downloadRecords();
			}
			if(cmd.hasOption("l")){
				//controller.linkRecords(Integer.parseInt(cmd.getOptionValue("l")));
				controller.linkRecords();
			}
			
			controller.testRef();
			
		} catch (ParseException e) {
			log.fatal("CommandLine parsing error");
		} catch (FileNotFoundException e) {
			log.fatal("FileNotFoundException");
			e.printStackTrace();
		} catch (XMLStreamException e) {
			log.fatal("XMLStreamException");
			e.printStackTrace();
		}
		
	}
}
