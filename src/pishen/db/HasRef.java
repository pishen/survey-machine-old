package pishen.db;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;


public class HasRef extends RelationshipShell{
	//DB keys
	private static final String CITATION_MARK = "CITATION_MARK"; //TODO remove
	private static final String CITATION = "CITATION";
	
	//private Relationship rel;
	
	protected HasRef(Relationship rel){
		super(rel);
		if(!rel.isType(RelType.HAS_REF)){
			throw new IllegalOperationException("[HAS_REF CONNECT] TYPE is wrong");
		}
		//this.rel = rel;
	}
	
	//TODO clean
	public void refactor(){
		int citation = super.getIntProperty(CITATION_MARK);
		super.removeProperty(CITATION_MARK);
		super.setProperty(CITATION, Integer.toString(citation));
	}
	
	public void setCitation(String citation){
		super.setProperty(CITATION, citation);
	}
	
	public void setCitation(int citation){
		super.setProperty(CITATION, citation);
	}
	
	public int getIntCitation(){
		return super.getIntProperty(CITATION);
	}
	
	public Reference getReference(){
		return new Reference(super.getEndNode());
	}
	
}
