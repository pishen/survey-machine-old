package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;

public class HasRefHits implements Iterator<HasRef>, Iterable<HasRef> {
	private Iterator<Relationship> iterator;
	private DBHandler dbHandler;
	
	public HasRefHits(Iterable<Relationship> iterable, DBHandler dbHandler){
		this.iterator = iterable.iterator();
	}

	@Override
	public Iterator<HasRef> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public HasRef next() {
		return new HasRef(iterator.next(), dbHandler);
	}

	@Override
	public void remove() {
		throw new IllegalOperationException("remove not implemented yet");
	}

}
