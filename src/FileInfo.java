/* The serializable file object */
/* by Jia Rao */
import java.io.Serializable;
public interface FileInfo extends Serializable {
	String getName();
	byte[] getContent();
	void setInfo(String name, byte[] content);
}
