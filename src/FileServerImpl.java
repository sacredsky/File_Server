/* Implementation of server methods */
/* by Jia Rao */
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FileServerImpl extends UnicastRemoteObject
implements FileServer
{
	FileServerImpl() throws RemoteException
	{
		super();
	}

	/**
	 * You need to implement the following file operations
	 * @throws IOException 
         */
	public FileInfo GetFile(String filename) throws RemoteException{
		FileInfo fileif1 = new FileOp();
		try
		{
			File file = new File(filename);
	        byte[] content = new byte[(int)file.length()];
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(file.getName()));
			input.read(content);
	        fileif1.setInfo(filename, content);
	        input.close();
	        return fileif1;
		}
		catch(Exception e) {
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
			return fileif1;
		}
        
	}

	public void PutFile(FileInfo fileif) throws RemoteException{

	}

	public void RenameFile(FileInfo fileif) throws RemoteException{

	}

	public void DeleteFile(FileInfo fileif) throws RemoteException{

	}

	public static void main(String args[])
	{
		//set the security manager
		try
		{
			//System.setSecurityManager(new RMISecurityManager());

			//create a local instance of the object
			FileServerImpl FileServer = new FileServerImpl();
			//FileServerImpl stub = (FileServerImpl) UnicastRemoteObject.exportObject(FileServer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("FILE-SERVER", FileServer);
            
			//put the local instance in the registry
			//Naming.rebind("FILE-SERVER" , FileServer);

			System.out.println("Server waiting.....");
		}
		//catch (java.net.MalformedURLException me)
		//{
		//	System.out.println("Malformed URL: " + me.toString());
		//}

		catch (RemoteException re)
		{
			System.out.println("Remote exception: " + re.toString());
		}
		
		catch (IOException fnfe)
		{
			System.out.println("IOException exception: " + fnfe.toString());
		}
	}
}


