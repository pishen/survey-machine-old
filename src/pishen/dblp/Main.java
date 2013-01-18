package pishen.dblp;

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
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%d{MM-dd HH:mm:ss}] %m%n")));
		Logger.getRootLogger().setLevel(Level.INFO);
		
		try {
			Options options = new Options();
			options.addOption("d", false, "download the papers");
			options.addOption("l", false, "link the citation network");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			
			new Controller().start();
		} catch (ParseException e) {
			log.fatal("CommandLine parsing error");
		}
		
		
		
	}
	
}
