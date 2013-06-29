package pishen.db;

import org.neo4j.graphdb.Node;

import pishen.exception.IllegalOperationException;



public class Reference extends NodeShell{
	//node type
	private static final String TYPE = "REFERENCE";
	//DB keys
	private static final String CONTENT = "CONTENT";
	private static final String LINKS = "LINKS";
	
	/*public static Reference createReference(){
		Node node = DBHandler.createNode();
		return new Reference(node);
	}*/
	
	public Reference(Node node, DBHandler dbHandler){
		super(node, dbHandler);
		/*if(super.isEmpty()){
			//initialize a new Reference
			super.setType(Reference.TYPE);
		}else{
			//connect an existed Reference
			if(!super.hasType() || !super.getType().equals(Reference.TYPE)){
				throw new IllegalOperationException("[REFERENCE_CONNECT] TYPE is wrong");
			}
		}*/
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
	
	public void delete(){
		super.delete();
	}
}
