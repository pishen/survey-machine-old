package pishen.db;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;


public class HasRef extends RelationshipShell{
	//DB keys
	private static final String CITATION = "CITATION";
	
	//private Relationship rel;
	
	protected HasRef(Relationship rel){
		super(rel);
		if(!rel.isType(RelType.HAS_REF)){
			throw new IllegalOperationException("[HAS_REF CONNECT] TYPE is wrong");
		}
		//this.rel = rel;
	}
	
	public void setCitation(String citation){
		super.setProperty(CITATION, citation);
	}
	
	public String getCitation(){
		return super.getStringProperty(CITATION);
	}
	
	public Reference getReference(){
		return new Reference(super.getEndNode());
	}
	
}
