/* File server interface */
/* by Jia Rao */
import java.io.FileNotFoundException;
import java.rmi.*;

public interface FileServer extends Remote
{
  public  FileInfo GetFile(String filename) throws RemoteException;
  public  void PutFile(FileInfo fileif) throws RemoteException;
  public void RenameFile(String Filename, String Filename2) throws RemoteException;
  public void DeleteFile(String Filename) throws RemoteException;
  public void mkdir(String Filename) throws RemoteException;
  public void rmdir(String Filename) throws RemoteException;
}
