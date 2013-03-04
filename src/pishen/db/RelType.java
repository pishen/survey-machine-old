package pishen.db;

import org.neo4j.graphdb.RelationshipType;

public enum RelType implements RelationshipType {
	HAS_REF, CITES
}
