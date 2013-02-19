package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

public class RecordHits implements Iterator<DBRecord>, Iterable<DBRecord> {
	private IndexHits<Node> indexHits;

	public RecordHits(IndexHits<Node> nodeIter){
		this.indexHits = nodeIter;
	}
	
	@Override
	public Iterator<DBRecord> iterator() {
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return indexHits.hasNext();
	}

	@Override
	public DBRecord next() {
		return new DBRecord(indexHits.next());
	}

	@Override
	public void remove() {
		//TODO remove all edges of the node and delete it
	}
	
	public void close(){
		indexHits.close();
	}

}
