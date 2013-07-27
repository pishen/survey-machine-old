package pishen.core;

import com.lexicalscope.jewel.cli.Option;

public interface CLIOptions {
	@Option
	double getHideRatio();
	
	@Option
	int getTopK();
}
