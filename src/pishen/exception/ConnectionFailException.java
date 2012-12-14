package pishen.exception;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ConnectionFailException extends IOException {

	private static final long serialVersionUID = 4404218934183691370L;
	private HttpURLConnection failConnection;
	
	public ConnectionFailException(HttpURLConnection urlc){
		failConnection = urlc;
	}
	
	public HttpURLConnection getFailConnection(){
		return failConnection;
	}

}
