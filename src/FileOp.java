/* File operations */
/* by Jia Rao */
public class FileOp implements FileInfo {
	private String name = null;
	private byte[] content = null;
	public String getName(){
		return name;
	}
	public byte[] getContent(){
		return content;
	}
	public void setInfo(String name, byte[] content){
		this.name = name;
		this.content = content;
	}
}
