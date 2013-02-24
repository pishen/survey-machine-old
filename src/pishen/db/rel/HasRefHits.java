package pishen.db.rel;

import java.util.Iterator;

import org.neo4j.graphdb.Relationship;

public class HasRefHits implements Iterator<HasRef>, Iterable<HasRef> {
	private Iterator<Relationship> iterator;
	
	public HasRefHits(Iterable<Relationship> iterable){
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
		return new HasRef(iterator.next());
	}

	@Override
	public void remove() {
		//not implemented yet
	}

}
