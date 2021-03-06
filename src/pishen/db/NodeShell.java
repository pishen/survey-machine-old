package pishen.db;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public abstract class NodeShell extends ContainerShell {
	//Key
	//private static final String TYPE = "TYPE";
	
	protected Node node;
	
	protected NodeShell(Node node, DBHandler dbHandler){
		super(node, dbHandler);
		this.node = node;
	}
	
	public long getId(){
		return node.getId();
	}
	
	/*protected void setType(String type){
		if(hasType()){
			throw new IllegalOperationException("Cannot change a set type");
		}else{
			super.setProperty(TYPE, type);
		}
	}
	
	protected boolean hasType(){
		return super.hasProperty(TYPE);
	}
	
	protected String getType(){
		return super.getStringProperty(TYPE);
	}*/
	
	protected Relationship createRelationshipTo(NodeShell targetNodeShell, RelType relType){
		Transaction tx = dbHandler.getTransaction();
		try {
			Relationship rel = node.createRelationshipTo(targetNodeShell.node, relType);
			tx.success();
			return rel;
		} finally {
			tx.finish();
		}
	}
	
	protected Iterable<Relationship> getRelationships(RelType relType){
		return node.getRelationships(relType);
	}
	
	protected Iterable<Relationship> getRelationships(RelType relType, Direction direction){
		return node.getRelationships(relType, direction);
	}
	
	protected void delete(){
		Transaction tx = dbHandler.getTransaction();
		try {
			for(Relationship rel: node.getRelationships()){
				rel.delete();
			}
			node.delete();
			tx.success();
		} finally {
			tx.finish();
		}
	}
}
