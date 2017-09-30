/* The client side of a RMI file server */
/* by Jia Rao */
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FileClient  
{
	public static void main(String[]  args)
	{
		if(args.length != 1) {
			System.out.println("Usage: java FileClient hostname");
			System.exit(0);
		}
		try
		{
			Registry registry = LocateRegistry.getRegistry(args[0]);
			String url = "//" + args[0] + "/FILE-SERVER";
            System.out.println(url);
			FileServer fs = (FileServer)registry.lookup("FILE-SERVER");

			System.out.println("Found remote server !");
			System.out.println("---------------------");
			System.out.println("Please issue commands.");
			System.out.println("get filename");
			System.out.println("put filename");
			System.out.println("delete filename");
			System.out.println("rename oldname newname");
			System.out.println("mkdir dirname");
			System.out.println("rmdir dirname");

			BufferedReader inFromUser = new BufferedReader (new InputStreamReader(System.in));
			String req = inFromUser.readLine();
			StringTokenizer st = new StringTokenizer(req);
			String cmd = st.nextToken();
			String filename = st.nextToken();

			if (cmd.equals("get")){
                                FileInfo fileif = fs.GetFile(filename);
				File file = new File(filename);
                                if(!file.exists()){
                                	file.createNewFile();
			        }
				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.getName()));
				output.write(fileif.getContent());
				output.flush();
				output.close();
			}
			else if (cmd.equals("put")){
				File file = new File(filename);
                                byte[] content = new byte[(int)file.length()];
				BufferedInputStream input = new BufferedInputStream(new FileInputStream(file.getName()));
				input.read(content);
				FileInfo fileif1 = new FileOp();
                                fileif1.setInfo(filename, content);
                                fs.PutFile(fileif1);
				input.close();
			}
			else if (cmd.equals("rename")){
				String filename2 = st.nextToken();
				fs.RenameFile(filename, filename2);
			}
			else if (cmd.equals("delete")){
				fs.DeleteFile(filename);
			}
			else if (cmd.equals("mkdir")){
				fs.mkdir(filename);
			}
			else if (cmd.equals("rmdir")){
				fs.rmdir(filename);
			}
		}
		catch(Exception e) {
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
		}
	}
}
