/* File server interface */
/* by Jia Rao */
import java.io.FileNotFoundException;
import java.rmi.*;

public interface FileServer extends Remote
{
  public  FileInfo GetFile(String filename) throws RemoteException;
  public  void PutFile(FileInfo fileif) throws RemoteException;
}
