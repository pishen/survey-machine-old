package pishen.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Downloader {
	private static final Logger log = Logger.getLogger(Downloader.class);
	private static final List<Proxy> proxyList = new ArrayList<Proxy>();
	private static final String[] userAgentStrs = {"Mozilla/5.0 Gecko/20100101 Firefox/17.0", 
		"Mozilla/5.0 AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.97 Safari/537.22",
		"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/534.57.7 (KHTML, like Gecko)",
		"Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:19.0) Gecko/20100101 Firefox/19.0"};
	
	static {
		initProxyList();
	}
	
	public static void downloadFileWithRetry(URL targetURL, OutputStream out, String expectedContentType) throws DownloadFailException{
		int retryPeriod = 2000;
		while(true){
			try {
				downloadFile(targetURL, out, expectedContentType);
				//sleep(500); //sleep for not querying the server too frequently
				return; //finish download if there's no exceptions
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
				//sleep(500); //sleep for not querying the server too frequently
				log.warn("wrong content type");
				log.warn("--content type: [" + e.getContentType() + "]");
				throw new DownloadFailException();
			} catch (IOException e) {
				log.error("IOException when downloading file", e);
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
		HttpURLConnection urlc = null;
		
		//randomly choose a proxy
		if(proxyList.isEmpty()){
			urlc = (HttpURLConnection)url.openConnection();
		}else{
			Proxy randomProxy = proxyList.get((int)(proxyList.size() * Math.random()));
			log.info("proxy=" + randomProxy.address());
			urlc = (HttpURLConnection)url.openConnection(randomProxy);
		}
		
		//randomly choose an user-agent
		urlc.setRequestProperty("User-Agent", userAgentStrs[(int)(userAgentStrs.length * Math.random())]);

		return urlc;
	}
	
	private static void downloadFile(URLConnection urlc, OutputStream out) throws IOException{
		byte[] buffer = new byte[4096];
		InputStream in = null;
		try {
			in = urlc.getInputStream();
			int n = 0;
			while((n = in.read(buffer)) > 0){
				out.write(buffer, 0, n);
			}
		} finally {
			in.close();
		}
	}
	
	private static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("exception on sleep", e);
		}
	}
	
	private static void initProxyList(){
		try {
			BufferedReader in = new BufferedReader(new FileReader("proxy-list"));
			String line = null;
			while((line = in.readLine()) != null){
				int port = Integer.parseInt(line);
				proxyList.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", port)));
			}
			in.close();
		} catch (Exception e) {
			log.error("error on reading proxy-list", e);
			System.exit(0);
		}
	}
	
	private static class DownloadFailException extends Exception{
		private static final long serialVersionUID = 1L;
	}
	
	private static class ConnectionFailException extends Exception{
		private static final long serialVersionUID = 1L;
		private HttpURLConnection failConnection;
		
		public ConnectionFailException(HttpURLConnection urlc){
			failConnection = urlc;
		}
		
		public HttpURLConnection getFailConnection(){
			return failConnection;
		}
		
		public String getResponseCode(){
			try {
				int responseCode = failConnection.getResponseCode();
				return "" + responseCode;
			} catch (IOException e) {
				return "cannot access";
			}
		}
	}
	
	private static class WrongContentTypeException extends Exception{
		private static final long serialVersionUID = 1L;
		private String contentType;
		
		public WrongContentTypeException(String contentType){
			this.contentType = contentType;
		}
		
		public String getContentType(){
			return contentType;
		}
	}
}
