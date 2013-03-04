package pishen.db;

import org.neo4j.graphdb.Relationship;


public class HasRef extends ContainerShell{
	//DB keys
	private static final String CITATION_MARK = "CITATION_MARK";
	
	//private Relationship rel;
	
	public HasRef(Relationship rel){
		super(rel);
		//this.rel = rel;
	}
	
	public void setCitationMark(int mark){
		super.setProperty(CITATION_MARK, mark);
	}
	
	public int getCitationMark(){
		return super.getIntProperty(CITATION_MARK);
	}
	
}
