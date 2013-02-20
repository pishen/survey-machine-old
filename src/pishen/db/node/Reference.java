package pishen.db.node;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import pishen.db.ContainerShell;

public class Reference extends ContainerShell{
	public static final String CONTENT = "CONTENT";
	public static final String LINKS = "LINKS";
	
	private Node node;
	
	public Reference(Node node){
		this.node = node;
	}
	
	public void setProperty(String key, Object value){
		if(value != null){
			Transaction tx = graphDB.beginTx();
			try {
				node.setProperty(key, value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	public void removeProperty(String key){
		Transaction tx = graphDB.beginTx();
		try {
			node.removeProperty(key);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public boolean hasProperty(String key){
		return node.hasProperty(key.toString());
	}
	
	public String getStringProperty(String key){
		return (String)getProperty(key);
	}
	
	public Object getProperty(String key){
		return node.getProperty(key.toString());
	}
}
