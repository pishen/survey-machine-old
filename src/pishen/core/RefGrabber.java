package pishen.core;

import org.apache.log4j.Logger;

import pishen.db.DBRecord;

public class RefGrabber {
	private static final Logger log = Logger.getLogger(RefGrabber.class);
	
	private static DBRecord currentRecord;
	
	public static void grabRef(DBRecord dbRecord){
		RefGrabber.currentRecord = dbRecord;
	}
}
