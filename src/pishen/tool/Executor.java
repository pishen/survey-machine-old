package pishen.tool;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;

public class Executor {
	private static final Logger log = Logger.getLogger(Executor.class);
	
	public static void execWithTimeout(String cmdLineStr, OutputStream subProcessOutput) throws IOException{
		CommandLine cmdLine = CommandLine.parse(cmdLineStr);
		
		ExecuteWatchdog watchdog = new ExecuteWatchdog(10000); //timeout in 10s 
		ExecuteStreamHandler streamHandler = new PumpStreamHandler(subProcessOutput);
		
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(streamHandler);

		log.info(cmdLineStr.split(" ")[0]);
		try {
			executor.execute(cmdLine);
		} catch (IOException e) {
			if(watchdog.killedProcess()){
				log.error("error: killed by watchdog");
			}else{
				log.error("error: unknown");
			}
			throw e;
		}
	}
}
