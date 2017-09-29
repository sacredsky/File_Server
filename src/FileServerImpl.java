/* Implementation of server methods */
/* by Jia Rao */

//import java.rmi.*;
//import java.rmi.server.*;
//import java.rmi.registry.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class FileServerImpl implements FileServer
{
	//FileServerImpl() throws RemoteException
	//{
	//	super();
	//}
	public FileServerImpl() {}
	/**
	 * You need to implement the following file operations
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
	        System.out.println("Sent "+filename+"!");
	        return fileif1;
		}
		catch(Exception e) {
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
			return fileif1;
		}
        
	}

	public void PutFile(FileInfo fileif) throws RemoteException{
		File file = new File(fileif.getName());
		try
		{
			if(!file.exists()){
			        	file.createNewFile();
			}
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.getName()));
			output.write(fileif.getContent());
			output.flush();
			output.close();
			System.out.println("Recieved "+fileif.getName()+"!");
			}
		catch(Exception e) {
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void RenameFile(String Filename, String Filename2) throws RemoteException{
		File file = new File(Filename);
		File file2 = new File(Filename2);
		boolean success = file.renameTo(file2);
		if (!success) {
			System.out.println("Rename "+Filename+" Error!");
		}
		else {
			System.out.println("Rename from "+Filename+" to "+Filename2+"!");
		}
	}

	public void DeleteFile(String Filename) throws RemoteException{
		File file = new File(Filename);
		boolean success = file.delete();
		if (!success) {
			System.out.println("Delete "+Filename+" Error!");
		}
		else {
			System.out.println("Delete "+Filename+" success!");
		}
	}

	public static void main(String args[])
	{
		//set the security manager
		try
		{
			//System.setSecurityManager(new RMISecurityManager());

			//create a local instance of the object
			FileServerImpl FileServer = new FileServerImpl();
			FileServer stub = (FileServer) UnicastRemoteObject.exportObject(FileServer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("FILE-SERVER", stub);
            
			//put the local instance in the registry
			//Naming.rebind("FILE-SERVER" , FileServer);
            System.err.println("Server ready");
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
		
		//catch (IOException fnfe)
		//{
		//	System.out.println("IOException exception: " + fnfe.toString());
		//}
	}
}


