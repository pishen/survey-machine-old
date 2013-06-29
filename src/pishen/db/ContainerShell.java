package pishen.db;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

abstract class ContainerShell {
	protected PropertyContainer container;
	protected DBHandler dbHandler;
	
	protected ContainerShell(PropertyContainer container, DBHandler dbHandler){
		this.container = container;
		this.dbHandler = dbHandler;
	}
	
	/*protected boolean isEmpty(){
		return !container.getPropertyKeys().iterator().hasNext();
	}*/
	
	protected void setProperty(String key, String value){
		if(value != null){
			Transaction tx = dbHandler.getTransaction();
			try {
				container.setProperty(key, value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	protected void setArrayProperty(String key, String[] value){
		if(value != null){
			Transaction tx = dbHandler.getTransaction();
			try {
				container.setProperty(key, value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	protected void removeProperty(String key){
		Transaction tx = dbHandler.getTransaction();
		try {
			container.removeProperty(key);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	protected boolean hasProperty(String key){
		return container.hasProperty(key);
	}
	
	protected String getProperty(String key){
		return (String)container.getProperty(key, null); //return null if property not exist
	}
	
	protected String[] getArrayProperty(String key){
		return (String[])container.getProperty(key, null); //return null if property not exist
	}
}
