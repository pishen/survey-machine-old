package pishen.db.node;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;


public class RecordHits implements Iterator<Record>, Iterable<Record> {
	private IndexHits<Node> indexHits;

	public RecordHits(IndexHits<Node> nodeIter){
		this.indexHits = nodeIter;
	}
	
	@Override
	public Iterator<Record> iterator() {
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return indexHits.hasNext();
	}

	@Override
	public Record next() {
		//TODO clean
		Node node = indexHits.next();
		if(node.getProperty("TYPE").equals("RECORD")){
			return new Record(node);
		}else{
			return null;
		}
	}

	@Override
	public void remove() {
		//TODO remove all edges of the node and delete it
	}
	
	public void close(){
		indexHits.close();
	}

}
