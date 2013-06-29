package pishen.db;

import java.util.Iterator;

import org.neo4j.graphdb.Relationship;

import pishen.exception.IllegalOperationException;

public class CiteHits implements Iterator<Cite>, Iterable<Cite> {
	private Iterator<Relationship> iterator;
	private DBHandler dbHandler;
	
	public CiteHits(Iterable<Relationship> iterable, DBHandler dbHandler){
		this.iterator = iterable.iterator();
		this.dbHandler = dbHandler;
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
		return new Cite(iterator.next(), dbHandler);
	}

	@Override
	public void remove() {
		throw new IllegalOperationException("remove not implemented yet");
	}

}
