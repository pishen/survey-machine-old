package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

import pishen.exception.IllegalOperationException;



public class RecordHits implements Iterator<Record>, Iterable<Record> {
	private IndexHits<Node> indexHits;
	private DBHandler dbHandler;
	private int count;

	public RecordHits(IndexHits<Node> nodeIter, DBHandler dbHandler){
		this.indexHits = nodeIter;
		this.dbHandler = dbHandler;
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
		return new Record(indexHits.next(), dbHandler);
	}

	@Override
	public void remove() {
		throw new IllegalOperationException("remove not implemented yet");
	}
	
	public int size(){
		return indexHits.size();
	}
	
	public void close(){
		indexHits.close();
	}
	
	public synchronized int count(){
		return ++count;
	}
	
	public synchronized Record getNextRecord(){
		if(hasNext()){
			return next(); 
		}else{
			return null;
		}
	}

}
