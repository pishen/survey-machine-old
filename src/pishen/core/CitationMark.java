package pishen.core;

import java.util.ArrayList;

import pishen.exception.IllegalOperationException;


public class CitationMark {
	//private static final Logger log = Logger.getLogger(CitationMark.class);
	public static enum Type{
		NUMBER, TEXT, UNKNOWN
	}
	
	private String content;
	private Type type;
	private ArrayList<String> citations = new ArrayList<String>();
	
	public CitationMark(String content){
		this.content = content;
		detectType();
		splitContent();
	}
	
	public Type getType(){
		return type;
	}
	
	public ArrayList<Integer> getIntCitations(){
		if(type == Type.NUMBER){
			ArrayList<Integer> intCitations = new ArrayList<Integer>();
			for(String citation: citations){
				intCitations.add(Integer.parseInt(citation));
			}
			return intCitations;
		}else{
			throw new IllegalOperationException("should not get int citation from non-NUMBER CitationMark");
		}
	}
	
	private void splitContent(){
		if(type == Type.NUMBER){
			String[] commaSeperatedStrs = content.split(",");
			for(String commaSeperatedStr: commaSeperatedStrs){
				if(commaSeperatedStr.contains("-")){
					String[] dashSeperatedStrs = commaSeperatedStr.split("-");
					int start = Integer.parseInt(dashSeperatedStrs[0]);
					int end = Integer.parseInt(dashSeperatedStrs[1]);
					for(int i = start; i <= end; i++){
						citations.add("" + i);
					}
				}else{
					citations.add(commaSeperatedStr);
				}
			}
		}
	}
	
	private void detectType(){
		String noSpaceContent = content.replaceAll(" ", "");
		if(noSpaceContent.matches("[1-9]\\d{0,2}")){
			//[1] - [999]
			type = Type.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(,[1-9]\\d{0,2})+")){
			//[1,2,3]
			type = Type.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}-[1-9]\\d{0,2}")){
			//[1-10]
			type = Type.NUMBER;
		}else if(noSpaceContent.matches("[1-9]\\d{0,2}(-[1-9]\\d{0,2})?(,[1-9]\\d{0,2}(-[1-9]\\d{0,2})?)+")){
			//[1,2-10,11,13]
			type = Type.NUMBER;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d")){
			//[1000] - [2999]
			type = Type.UNKNOWN;
		}else if(noSpaceContent.matches("[12]\\d\\d\\d(;[12]\\d\\d\\d)+")){
			//[1988;2005;2013]
			type = Type.UNKNOWN;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d")){
			//[pishen tsai et al. 2013]
			type = Type.UNKNOWN;
		}else if(noSpaceContent.matches("\\D+[12]\\d\\d\\d(;\\D+[12]\\d\\d\\d)+")){
			//[pishen tsai et al. 2013; pj cheng 2007]
			type = Type.UNKNOWN;
		}else{
			type = Type.UNKNOWN;
		}
	}
}
