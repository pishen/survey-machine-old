package pishen.core;

import com.lexicalscope.jewel.cli.Option;

public interface CLIOptions {
	@Option(shortName="d", description="download the papers")
	boolean isDownloadRecords();
	
	@Option(shortName="f", description="fetch the reference list of papers")
	boolean isFetchReferences();
	
	@Option(shortName="p", defaultValue="1", description="speed up downloadRecords and fetchReferences")
	int getParallel();
	
	@Option(shortName="c")
	boolean isConnectRecords();
	
	@Option(shortName="t")
	boolean isTest();
	
	@Option(shortName="e")
	boolean isEval();
}
