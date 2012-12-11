package pishen.dblp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class EEHandler {
	private HashMap<String, Integer> domainNameMap = new HashMap<String, Integer>();
	private ArrayList<String> domainNameList = new ArrayList<String>();
	private int dblpNotPDF;
	private int notDBorHTTP;
	
	public EEHandler(){
		File archiveDir = new File("record-archive");
		if(!archiveDir.exists()){
			archiveDir.mkdir();
		}
	}
	
	public boolean downloadRecord(String recordKey, String ee){
		File recordFile = new File("record-archive/" + recordKey);
		
		if(recordFile.exists()){
			return true;
		}
		
		if(ee.startsWith("db")){
			ee = "http://www.sigmod.org/dblp/" + ee;
		}
		
		URL eeURL = createURL(ee);
		
		URL pdfURL = null;
		//handle different cases of publishers
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = ee.substring(ee.lastIndexOf(".") + 1);
			try {
				pdfURL = createURL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID + "&type=pdf");
				URLConnection pdfConnect = pdfURL.openConnection();
				addUserAgent(pdfConnect);
				if(pdfConnect.getContentType().equals("application/pdf")){
					byte[] buffer = new byte[4096];
					InputStream in = pdfConnect.getInputStream();
					OutputStream out = new FileOutputStream("record-archive/" + recordKey + ".pdf");
					int n = 0;
					while((n = in.read(buffer)) > 0){
						out.write(buffer, 0, n);
					}
					in.close();
					out.close();
					return true;
				}else{
					//TODO wrong rule
					System.out.println("rule error!");
					System.out.println("ee: " + ee);
					System.out.println("pdfConnect: " + pdfConnect.getURL());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return false;
	}
	
	private URL createURL(String urlStr){
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private void addUserAgent(URLConnection urlc){
		urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:17.0) Gecko/20100101 Firefox/17.0");
	}
	
	//analysis
	public void addEE(String ee){
		if(ee.startsWith("db")){
			ee = "http://www.sigmod.org/dblp/" + ee;
			if(!ee.endsWith(".pdf")){
				dblpNotPDF++;
			}
		}else if(!ee.startsWith("http")){
			notDBorHTTP++;
		}
		
		try {
			String domainName = (new URL(ee)).getHost();
			//System.out.println(domainName);
			if(domainNameMap.containsKey(domainName)){
				domainNameMap.put(domainName, domainNameMap.get(domainName).intValue() + 1);
			}else{
				domainNameMap.put(domainName, 1);
				domainNameList.add(domainName);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void printResult(){
		System.out.println("dblpNotPDF=" + dblpNotPDF + ", notDBorHTTP=" + notDBorHTTP);
		System.out.println("# of unique domain names=" + domainNameMap.size());
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("domainnames"));
			for(String domain: domainNameList){
				if(domainNameMap.get(domain) >= 100){
					out.write(domain + ": " + domainNameMap.get(domain));
					out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
