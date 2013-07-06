package pishen.db;

import org.neo4j.graphdb.Node;

import pishen.exception.IllegalOperationException;



public class Reference extends NodeShell{
	//node type
	private static final String TYPE = "REFERENCE";
	//DB keys
	private static final String REF_INDEX = "REF_INDEX";
	private static final String CONTENT = "CONTENT";
	private static final String LINKS = "LINKS";
	
	public Reference(Node node, DBHandler dbHandler){
		super(node, dbHandler);
	}
	
	public void setIndex(int index){
		super.setProperty(REF_INDEX, Integer.toString(index));
	}
	
	public int getIndex(){
		return Integer.parseInt(super.getProperty(REF_INDEX));
	}
	
	public void setContent(String content){
		super.setProperty(CONTENT, content);
	}
	
	public String getContent(){
		return super.getProperty(CONTENT);
	}
	
	public void setLinks(String[] links){
		super.setArrayProperty(LINKS, links);
	}
	
	public String[] getLinks(){
		return super.getArrayProperty(LINKS);
	}
}
