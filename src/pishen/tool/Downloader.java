package pishen.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import pishen.exception.ConnectionFailException;
import pishen.exception.DownloadFailException;
import pishen.exception.WrongContentTypeException;

public class Downloader {
	private static final Logger log = Logger.getLogger(Downloader.class);
	
	public static void downloadFileWithRetry(URL targetURL, OutputStream out, String expectedContentType) throws DownloadFailException{
		int retryPeriod = 2000;
		while(true){
			try {
				downloadFile(targetURL, out, expectedContentType);
				sleep(1000); //sleep 1s for not querying the server too frequently
				break; //finish download if there's no exceptions
			} catch (ConnectionFailException e) {
				//sleep and retry, if fail too many times, print fail messages
				if(retryPeriod <= 32000){
					log.warn("connection failed, retry in " + (retryPeriod / 1000) + "s");
					sleep(retryPeriod);
					retryPeriod *= 2;
				}else{
					log.warn("connection permanently failed");
					log.warn("--URL: " + e.getFailConnection().getURL());
					log.warn("--response: " + e.getResponseCode());
					throw new DownloadFailException();
				}
			} catch (WrongContentTypeException e) {
				//content type is wrong
				sleep(1000); //sleep 1s for not querying the server too frequently
				log.warn("wrong content type");
				log.warn("--content type: [" + e.getContentType() + "]");
				throw new DownloadFailException();
			} catch (IOException e) {
				log.error("IOException when downloading");
				e.printStackTrace();
				throw new DownloadFailException();
			}
		}
	}
	
	private static void downloadFile(URL targetURL, OutputStream out, String expectedContentType) 
			throws IOException, ConnectionFailException, WrongContentTypeException{
		HttpURLConnection urlc = createURLConnection(targetURL);
		
		if(urlc.getContentType() == null){
			throw new ConnectionFailException(urlc);
		}else if(!urlc.getContentType().startsWith(expectedContentType)){
			throw new WrongContentTypeException(urlc.getContentType());
		}else{
			downloadFile(urlc, out);
		}
	}
	
	private static HttpURLConnection createURLConnection(URL url) throws IOException{
		HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
		urlc.setRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100101 Firefox/17.0");
		return urlc;
	}
	
	private static void downloadFile(URLConnection urlc, OutputStream out) throws IOException{
		byte[] buffer = new byte[4096];
		InputStream in = urlc.getInputStream();
		int n = 0;
		while((n = in.read(buffer)) > 0){
			out.write(buffer, 0, n);
		}
		in.close();
		out.close();
	}
	
	private static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("exception on sleep");
			e.printStackTrace();
		}
	}
}
