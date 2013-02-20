package pishen.db;

import org.neo4j.graphdb.GraphDatabaseService;

import pishen.db.node.Reference;

public class ContainerShell {
	protected static GraphDatabaseService graphDB;
	
	public static void setGraphDB(GraphDatabaseService graphDB){
		ContainerShell.graphDB = graphDB;
	}
}
