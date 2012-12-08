package pishen.dblp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
		
		return false;
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
