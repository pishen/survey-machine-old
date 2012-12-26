package pishen.dblp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.log4j.Logger;

import pishen.exception.ConnectionFailException;
import pishen.exception.DownloadFailException;
import pishen.exception.MismatchedRuleException;
import pishen.exception.UndefinedRuleException;

public class EEHandler {
	private static final Logger log = Logger.getLogger(EEHandler.class);
	private static final String TEXT_RECORD_DIR = "text-records";
	private static final String PDF_RECORD_DIR = "pdf-records";
	private Record record;
	private File textRecord, pdfRecord;
	
	public EEHandler(){
		File textRecordDir = new File(TEXT_RECORD_DIR);
		if(!textRecordDir.exists()){
			textRecordDir.mkdir();
		}
		File pdfRecordDir = new File(PDF_RECORD_DIR);
		if(!pdfRecordDir.exists()){
			pdfRecordDir.mkdir();
		}
	}
	
	public void downloadRecord(Record record) throws DownloadFailException, InterruptedException, IOException{		
		this.record = record;
		textRecord = new File(TEXT_RECORD_DIR + "/" + record.getProperty(Key.FILENAME));
		
		if(textRecord.exists()){
			log.info("record exists");
			return;
		}
		
		pdfRecord = new File(PDF_RECORD_DIR + "/" + record.getProperty(Key.FILENAME) + ".pdf");
		
		log.info("downloading PDF");
		downloadPDFWithRetry();
		
		//TODO check the embedded fonts by pdffonts
		
		log.info("converting PDF to Text");
		pdfToText();
		
	}
	
	private void downloadPDFWithRetry() throws DownloadFailException, InterruptedException, IOException{
		int retryPeriod = 2000;
		while(true){
			try {
				downloadPDF();
				Thread.sleep(1000); //sleep 1s for not querying the server too frequently
				break; //finish download if there's no exceptions
			} catch (ConnectionFailException e) {
				//sleep and retry, if fail too many times, print fail messages
				if(retryPeriod <= 32000){
					log.warn("connection failed, retry in " + (retryPeriod / 1000) + "s");
					Thread.sleep(retryPeriod);
					retryPeriod *= 2;
				}else{
					log.warn("connection permanently failed");
					log.warn("--URL: " + e.getFailConnection().getURL());
					log.warn("--response: " + (e.getFailConnection().getResponseCode()));
					throw new DownloadFailException();
				}
			} catch (MismatchedRuleException e) {
				//content type is wrong
				Thread.sleep(1000); //sleep 1s for not querying the server too frequently
				log.warn("undefined rule");
				log.warn("--content type: [" + e.getUndefinedConnection().getContentType() + "]");
				throw new DownloadFailException();
			} catch (UndefinedRuleException e) {
				throw new DownloadFailException();
			}
		}
	}
	
	private void downloadPDF() throws ConnectionFailException, MismatchedRuleException, IOException, UndefinedRuleException {
		String eeStr = (String)record.getProperty(Key.EE);
		URL eeURL = new URL(eeStr);
		//handle different cases of publishers
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = eeStr.substring(eeStr.lastIndexOf(".") + 1);
			URL pdfURL = new URL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID);
			HttpURLConnection pdfConnection = createURLConnection(pdfURL);
			
			if(pdfConnection.getContentType() == null){
				throw new ConnectionFailException(pdfConnection);
			}
			
			//TODO other useful content types?
			if(pdfConnection.getContentType().equals("application/pdf")){
				downloadFromURLConnect(pdfConnection, pdfRecord);
			}else{
				throw new MismatchedRuleException(pdfConnection);
			}
			
		}else{
			throw new UndefinedRuleException();
		}
		//TODO handle other domain names
	}
	
	private void pdfToText() throws DownloadFailException{
		//TODO check if the PDF is scanned version?
		String line = "pdftotext " + pdfRecord.getAbsolutePath() + " " + textRecord.getAbsolutePath();
		CommandLine cmdLine = CommandLine.parse(line);
		
		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(10000); //timeout in 10s 
		executor.setWatchdog(watchdog);
		
		try {
			executor.execute(cmdLine);
		} catch (IOException e) {
			if(watchdog.killedProcess()){
				System.out.println("error on pdftotext, killed by watchdog");
			}else{
				System.out.println("error on pdftotext");
			}
			textRecord.delete();
			throw new DownloadFailException();
		} finally {
			pdfRecord.delete();
		}
		
	}
	
	private HttpURLConnection createURLConnection(URL url) throws IOException{
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:17.0) Gecko/20100101 Firefox/17.0");
		return urlc;
	}
	
	private void downloadFromURLConnect(URLConnection urlc, File outputFile) throws IOException{
		byte[] buffer = new byte[4096];
		InputStream in = urlc.getInputStream();
		OutputStream out = new FileOutputStream(outputFile);
		int n = 0;
		while((n = in.read(buffer)) > 0){
			out.write(buffer, 0, n);
		}
		in.close();
		out.close();
	}
	
}
