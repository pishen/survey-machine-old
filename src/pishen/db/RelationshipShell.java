package pishen.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class RelationshipShell extends ContainerShell {
	private Relationship rel;

	protected RelationshipShell(Relationship rel) {
		super(rel);
		this.rel = rel;
	}

	protected Node getEndNode(){
		return rel.getEndNode();
	}
}
