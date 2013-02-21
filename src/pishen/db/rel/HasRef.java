package pishen.db.rel;

import org.neo4j.graphdb.Relationship;

import pishen.db.ContainerShell;

public class HasRef extends ContainerShell{
	public static final String CITATION_MARK = "CITATION_MARK";
	
	//private Relationship rel;
	
	public HasRef(Relationship rel){
		super(rel);
		//this.rel = rel;
	}
	
	public void setProperty(String key, Object value){
		super.setProperty(key, value);
	}
	
	public String getStringProperty(String key){
		return super.getStringProperty(key);
	}
}
