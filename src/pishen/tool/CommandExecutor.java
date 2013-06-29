package pishen.tool;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;

public class CommandExecutor {
	private static final Logger log = Logger.getLogger(CommandExecutor.class);
	
	public static void exec(String command) throws IOException{
		exec(command, null, 0);
	}
	
	public static void exec(String command, OutputStream redirectOutput) throws IOException{
		exec(command, redirectOutput, 0);
	}
	
	public static void exec(String command, OutputStream redirectOutput, long timeout) throws IOException{
		CommandLine cmdLine = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();
		
		if(timeout > 0){
			executor.setWatchdog(new ExecuteWatchdog(timeout));
		}
		
		if(redirectOutput != null){
			executor.setStreamHandler(new PumpStreamHandler(redirectOutput));
		}
		
		log.info(command);
		try {
			executor.execute(cmdLine);
		} catch (IOException e) {
			log.error("exec error", e);
			throw e;
		}
	}
}
