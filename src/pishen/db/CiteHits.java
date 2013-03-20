package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;

public class CiteHits implements Iterator<Cite>, Iterable<Cite> {
	private Iterator<Relationship> iterator;
	
	public CiteHits(Iterable<Relationship> iterable){
		this.iterator = iterable.iterator();
	}
	
	@Override
	public Iterator<Cite> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Cite next(){
		return new Cite(iterator.next());
	}

	@Override
	public void remove() {
		throw new IllegalOperationException("remove not implemented yet");
	}

}
