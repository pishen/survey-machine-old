package pishen.dblp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import pishen.exception.ConnectionFailException;
import pishen.exception.DownloadFailException;
import pishen.exception.UndefinedRuleException;

public class EEHandler {
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
		textRecord = new File(TEXT_RECORD_DIR + "/" + record.getDashKey());
		
		if(textRecord.exists()){
			System.out.println("file exists");
			return;
		}
		
		pdfRecord = new File(PDF_RECORD_DIR + "/" + record.getDashKey() + ".pdf");
		
		downloadPDFWithRetry();
		pdfToText();

	}
	
	private void downloadPDFWithRetry() throws DownloadFailException, InterruptedException, IOException{
		int retryPeriod = 2000;
		while(true){
			try {
				downloadPDF();
				break; //finish download if there's no exceptions
			} catch (ConnectionFailException e) {
				//sleep and retry, if fail too many times, print fail messages
				if(retryPeriod <= 32000){
					System.out.println("connection failed, retry in " + (retryPeriod / 1000) + " seconds");
					Thread.sleep(retryPeriod);
					retryPeriod *= 2;
				}else{
					System.out.println("connection permanently failed");
					System.out.println("URL: " + e.getFailConnection().getURL());
					System.out.println("response: " + (e.getFailConnection().getResponseCode()));
					throw new DownloadFailException();
				}
			} catch (UndefinedRuleException e) {
				//content type is wrong
				System.out.println("undefined rule");
				System.out.println("content type: " + e.getUndefinedConnection().getContentType());
				throw new DownloadFailException();
			}
		}
	}
	
	private void downloadPDF() throws ConnectionFailException, UndefinedRuleException, IOException, DownloadFailException {
		URL eeURL = new URL(record.getEEStr());
		//handle different cases of publishers
		if(eeURL.getHost().equals("doi.acm.org")){
			String documentID = record.getEEStr().substring(record.getEEStr().lastIndexOf(".") + 1);
			URL pdfURL = new URL("http://dl.acm.org/ft_gateway.cfm?id=" + documentID + "&type=pdf");
			HttpURLConnection pdfConnection = createURLConnection(pdfURL);
			
			if(pdfConnection.getContentType() == null){
				throw new ConnectionFailException(pdfConnection);
			}
			
			String contentType = pdfConnection.getContentType();
			if(!contentType.equals("application/pdf") && !contentType.equals("text/html")){
				throw new UndefinedRuleException(pdfConnection);
			}
			
			if(pdfConnection.getContentType().equals("application/pdf")){
				downloadFromURLConnect(pdfConnection, pdfRecord);
			}
			//TODO other useful content types?
		}else{
			throw new DownloadFailException();
		}
		//TODO handle other domain names
	}
	
	private void pdfToText() throws InterruptedException, IOException{
		//TODO check if the PDF is scanned version?
		Process pdftotext = new ProcessBuilder("pdftotext", pdfRecord.getAbsolutePath(), textRecord.getAbsolutePath()).start();
		pdftotext.waitFor();
		pdfRecord.delete();
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
