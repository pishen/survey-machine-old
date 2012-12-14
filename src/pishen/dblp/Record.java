package pishen.dblp;

public class Record {
	private String slashKey;
	private String dashKey;
	private String eeStr;
	
	public String getSlashKey(){
		return slashKey;
	}
	
	public String getDashKey(){
		return dashKey;
	}
	
	public String getEEStr(){
		return eeStr;
	}
	
	public void setSlashKey(String slashKey){
		this.slashKey = slashKey;
		dashKey = slashKey.replaceAll("/", "-");
	}
	
	public void setEEStr(String eeStr){
		if(eeStr.startsWith("db")){
			this.eeStr = "http://www.sigmod.org/dblp/" + eeStr;
		}else{
			this.eeStr = eeStr;
		}
	}
}
