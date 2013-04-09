package pishen.db;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;

public class Cite extends RelationshipShell {
	//DB keys
	private static final String CITATION = "CITATION";

	//create new Cite
	protected Cite(Relationship rel, String citation) {
		super(rel);
		if(!rel.isType(RelType.CITE) || !super.isEmpty()){
			throw new IllegalOperationException("[CITE_INIT] Relationship is not CITE/empty");
		}
		super.setProperty(CITATION, citation);
	}
	
	//connect existed Cist
	protected Cite(Relationship rel){
		super(rel);
		if(!rel.isType(RelType.CITE)){
			throw new IllegalOperationException("[CITE_CONNECT] Relationship is not CITE");
		}
	}
	
	public String getStringCitation(){
		return super.getStringProperty(CITATION);
	}
	
	public Record getStartRecord(){
		return new Record(super.getStartNode());
	}
	
	public Record getEndRecord(){
		return new Record(super.getEndNode());
	}

	@Override
	public void delete() {
		super.delete();
	}
	
}
