package pishen.dblp;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class Main {
	//private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("[%-5p] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);
		
		new Controller().start();
		
		/*
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(PDDocument.load("pdf-records/journals-toct-Razborov09.pdf"), new BufferedWriter(new FileWriter("pdf-output")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		/*
		try {
			URL test = new URL("http://dl.acm.org/ft_gateway.cfm?id=1376809&type=pdf");
			URLConnection con = test.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:17.0) Gecko/20100101 Firefox/17.0");
			System.out.println("content-type=" + con.getContentType());
			System.out.println("url=" + con.getURL());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while((line = in.readLine()) != null){
				System.out.println(line);
			}
			in.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
}
