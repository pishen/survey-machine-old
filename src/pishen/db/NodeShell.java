package pishen.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import pishen.db.rel.RelType;

public class NodeShell extends ContainerShell {
	private Node node;
	
	protected NodeShell(Node node){
		super(node);
		this.node = node;
	}
	
	protected Relationship createRelationshipTo(NodeShell targetNodeShell, RelType relType){
		Transaction tx = DBHandler.getTransaction();
		try {
			Relationship rel = node.createRelationshipTo(targetNodeShell.node, relType);
			tx.success();
			return rel;
		} finally {
			tx.finish();
		}
	}
}