package pishen.exception;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UndefinedRuleException extends IOException {
	private static final long serialVersionUID = -6696128831119290034L;

	private HttpURLConnection undefinedConnection;
	
	public UndefinedRuleException(HttpURLConnection urlc){
		undefinedConnection = urlc;
	}
	
	public HttpURLConnection getUndefinedConnection(){
		return undefinedConnection;
	}
}
