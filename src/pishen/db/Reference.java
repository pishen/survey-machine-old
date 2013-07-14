package pishen.db;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;



public class Reference extends NodeShell{
	//node type
	private static final String REFERENCE = "REFERENCE";
	//DB keys
	private static final String REF_INDEX = "REF_INDEX";
	private static final String CONTENT = "CONTENT";
	private static final String LINKS = "LINKS";
	
	public static Reference createReference(DBHandler dbHandler){
		Transaction tx = dbHandler.getTransaction();
		try{
			Node node = dbHandler.createNode();
			dbHandler.setNodeType(node, REFERENCE);
			Reference newReference = new Reference(node, dbHandler);
			tx.success();
			return newReference;
		}finally{
			tx.finish();
		}
	}
	
	public Reference(Node node, DBHandler dbHandler){
		super(node, dbHandler);
	}
	
	public void setIndex(int index){
		super.setProperty(REF_INDEX, Integer.toString(index));
	}
	
	public int getIndex(){
		return Integer.parseInt(super.getProperty(REF_INDEX));
	}
	
	public void setContent(String content){
		super.setProperty(CONTENT, content);
	}
	
	public String getContent(){
		return super.getProperty(CONTENT);
	}
	
	public void setLinks(String[] links){
		super.setArrayProperty(LINKS, links);
	}
	
	public String[] getLinks(){
		return super.getArrayProperty(LINKS);
	}
	
	public void createRefTo(Record targetRecord){
		super.createRelationshipTo(targetRecord, RelType.REF);
	}
	
	public Record getTargetRecord(){
		Record targetRecord = null;
		for(Relationship rel: super.getRelationships(RelType.REF, Direction.OUTGOING)){
			targetRecord = new Record(rel.getEndNode(), dbHandler);
		}
		return targetRecord;
	}
}
