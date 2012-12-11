package pishen.dblp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Main {
	public static void main(String[] args) {
		new Controller().start();
		
		/*
		try {
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(PDDocument.load("wsdm11-citation-recommend.pdf"), new BufferedWriter(new FileWriter("pdf-output")));
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
