package pishen.db;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

public class ContainerShell {
	private PropertyContainer container;
	
	protected ContainerShell(PropertyContainer container){
		this.container = container;
	}
	
	protected void setProperty(String key, Object value){
		if(value != null){
			Transaction tx = DBHandler.getTransaction();
			try {
				container.setProperty(key, value);
				tx.success();
			} finally {
				tx.finish();
			}
		}
	}
	
	protected void removeProperty(String key){
		Transaction tx = DBHandler.getTransaction();
		try {
			container.removeProperty(key);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	protected boolean hasProperty(String key){
		return container.hasProperty(key.toString());
	}
	
	protected String getStringProperty(String key){
		return (String)getProperty(key);
	}
	
	protected boolean getBooleanProperty(String key){
		return (Boolean)getProperty(key);
	}
	
	protected Object getProperty(String key){
		return container.getProperty(key.toString());
	}
}
