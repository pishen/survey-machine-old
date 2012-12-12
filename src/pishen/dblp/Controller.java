package pishen.dblp;

public class Controller {
	public static Controller currentController;
	
	private XMLParser xmlParser;
	//private DBHandler dbHandler;
	
	public void start(){
		currentController = this;
		
		xmlParser = new XMLParser();
		//dbHandler = new DBHandler();
		
		//dbHandler.startGraphDB();
		
		xmlParser.startParsing();
		
	}
}
