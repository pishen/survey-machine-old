package pishen.exception;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MismatchedRuleException extends IOException {
	private static final long serialVersionUID = -6696128831119290034L;

	private HttpURLConnection undefinedConnection;
	
	public MismatchedRuleException(HttpURLConnection urlc){
		undefinedConnection = urlc;
	}
	
	public HttpURLConnection getUndefinedConnection(){
		return undefinedConnection;
	}
}
