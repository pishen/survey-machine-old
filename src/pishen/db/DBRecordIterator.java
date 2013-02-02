package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Node;

public class DBRecordIterator implements Iterator<DBRecord> {
	private Iterator<Node> nodeIter;

	public DBRecordIterator(Iterator<Node> nodeIter){
		this.nodeIter = nodeIter;
	}
	
	@Override
	public boolean hasNext() {
		return nodeIter.hasNext();
	}

	@Override
	public DBRecord next() {
		return new DBRecord(nodeIter.next());
	}

	@Override
	public void remove() {
		//TODO remove all edges of the node and delete it
	}

}
