package pishen.dblp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class EEHandler {
	private HashMap<String, Integer> domainNameMap = new HashMap<String, Integer>();
	private ArrayList<String> domainNameList = new ArrayList<String>();
	private int dblpNotPDF;
	private int notDBorHTTP;
	
	private static final String TEXT_RECORD_DIR = "text-records";
	private static final String PDF_RECORD_DIR = "pdf-records";
	private PrintWriter wrongRuleWriter;
	private PDFTextStripper stripper;
	
	public EEHandler(){
		File textRecordDir = new File(TEXT_RECORD_DIR);
		if(!textRecordDir.exists()){
			textRecordDir.mkdir();
		}
		File pdfRecordDir = new File(PDF_RECORD_DIR);
		if(!pdfRecordDir.exists()){
			pdfRecordDir.mkdir();
		}
		
		try {
			wrongRuleWriter = new PrintWriter(new BufferedWriter(new FileWriter("wrong-rules")));
			stripper = new PDFTextStripper();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public boolean downloadRecord(String recordKey, String ee){
		File textRecord = new File(TEXT_RECORD_DIR + "/" + recordKey);
		
		if(textRecord.exists()){
			return true;
		}
		
		if(ee.startsWith("db")){
			ee = "http://www.sigmod.org/dblp/" + ee;
		}
		
		File pdfRecord = downloadPDF(recordKey, createURL(ee));
		
		if(pdfRecord.exists()){
			try {
				stripper.writeText(PDDocument.load(pdfRecord), new BufferedWriter(new FileWriter(textRecord)));
				stripper.resetEngine();
				pdfRecord.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}
	}
	
	private File downloadPDF(String recordKey, URL eeURL){
		File pdfRecord = new File(PDF_RECORD_DIR + "/" + recordKey + ".pdf");
		String eeStr = eeURL.toString();
		//handle different cases of publishers
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = eeStr.substring(eeStr.lastIndexOf(".") + 1);
			URL pdfURL = createURL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID + "&type=pdf");
			URLConnection pdfConnect = createURLConnect(pdfURL);
			if(pdfConnect.getContentType().equals("application/pdf")){
				downloadFromURLConnect(pdfConnect, pdfRecord);
			}else{
				//wrong rule
				wrongRuleWriter.println(recordKey);
			}
		}
		//TODO handle other cases
		return pdfRecord;
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
	
	private URLConnection createURLConnect(URL url){
		try {
			URLConnection urlc = url.openConnection();
			urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:17.0) Gecko/20100101 Firefox/17.0");
			return urlc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void downloadFromURLConnect(URLConnection urlc, File outputFile){
		byte[] buffer = new byte[4096];

		try {
			InputStream in = urlc.getInputStream();
			OutputStream out = new FileOutputStream(outputFile);
			int n = 0;
			while((n = in.read(buffer)) > 0){
				out.write(buffer, 0, n);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
