package pishen.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;


public class NodeIndexShell {
	private Index<Node> index;
	private DBHandler dbHandler;
	
	public NodeIndexShell(Index<Node> index, DBHandler dbHandler){
		this.index = index;
		this.dbHandler = dbHandler;
	}
	
	public IndexHits<Node> get(String key, String value){
		return index.get(key, value);
	}
	
	public void add(Node node, String key, String value){
		Transaction tx = dbHandler.getTransaction();
		try{
			index.remove(node, key);
			index.add(node, key, value);
			tx.success();
		}finally{
			tx.finish();
		}
	}
	
	public void remove(Node node){
		Transaction tx = dbHandler.getTransaction();
		try{
			index.remove(node);
			tx.success();
		}finally{
			tx.finish();
		}
	}
	
	/*public void delete(){
		Transaction tx = dbHandler.getTransaction();
		try{
			index.delete();
			tx.success();
		}finally{
			tx.finish();
		}
	}*/

}
