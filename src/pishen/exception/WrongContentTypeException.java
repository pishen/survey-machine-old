package pishen.exception;


public class WrongContentTypeException extends Exception {
	private static final long serialVersionUID = -6099197792586878879L;
	
	private String contentType;
	
	public WrongContentTypeException(String contentType){
		this.contentType = contentType;
	}
	
	public String getContentType(){
		return contentType;
	}
}
