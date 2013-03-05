package pishen.core;

import pishen.db.Record;

public class CitationMark {
	//private static final Logger log = Logger.getLogger(CitationMark.class);
	
	public static Record.CitationType typeOf(String content){
		String noSpaceContent = content.replaceAll(" ", "");
		if(noSpaceContent.matches("[1-9]\\d{0,2}")){
			//[1] - [999]
			return Record.CitationType.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(,[1-9]\\d{0,2})+")){
			//[1,2,3]
			return Record.CitationType.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}-[1-9]\\d{0,2}")){
			//[1-10]
			return Record.CitationType.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(-[1-9]\\d{0,2})?(,[1-9]\\d{0,2}(-[1-9]\\d{0,2})?)+")){
			//[1,2-10,11,13]
			return Record.CitationType.NUMBER;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d")){
			//[1000] - [2999]
			return Record.CitationType.TEXT;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d(;[12]\\d\\d\\d)+")){
			//[1988;2005;2013]
			return Record.CitationType.TEXT;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d")){
			//[pishen tsai et al. 2013]
			return Record.CitationType.TEXT;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d(;\\D+[12]\\d\\d\\d)+")){
			//[pishen tsai et al. 2013; pj cheng 2007]
			return Record.CitationType.TEXT;
		}else{
			return Record.CitationType.UNKNOWN;
		}
	}
}
