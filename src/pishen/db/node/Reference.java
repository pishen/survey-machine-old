package pishen.db.node;

import org.neo4j.graphdb.Node;

import pishen.db.NodeShell;

public class Reference extends NodeShell{
	public static final String CONTENT = "CONTENT";
	public static final String LINKS = "LINKS";
	
	//private Node node;
	
	public Reference(Node node){
		super(node);
		//this.node = node;
	}
	
	public boolean hasProperty(String key){
		return super.hasProperty(key);
	}

	public String getStringProperty(String key){
		return super.getStringProperty(key);
	}
	
	public void setProperty(String key, Object value){
		super.setProperty(key, value);
	}
	
}
