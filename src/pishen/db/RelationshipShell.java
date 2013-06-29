package pishen.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class RelationshipShell extends ContainerShell {
	private Relationship rel;

	protected RelationshipShell(Relationship rel, DBHandler dbHandler) {
		super(rel, dbHandler);
		this.rel = rel;
	}
	
	protected Node getStartNode(){
		return rel.getStartNode();
	}

	protected Node getEndNode(){
		return rel.getEndNode();
	}
	
	protected void delete(){
		Transaction tx = dbHandler.getTransaction();
		try{
			rel.delete();
			tx.success();
		}finally{
			tx.finish();
		}
	}
}
