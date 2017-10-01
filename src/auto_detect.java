
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.util.*;


public class auto_detect {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean loop;
    private boolean trace = false;
    private FileServer fs;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    auto_detect(Path dir, boolean loop, FileServer fs) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.loop = loop;
        this.fs=fs;

        if (loop) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s Detected! Updating to Server...\n", event.kind().name(), child);
        		try
        		{
        			//System.out.format("name %s\nchild  %s\n", name, child);
        			String filename = name.toString();
	                if (kind == ENTRY_CREATE||kind == ENTRY_MODIFY) {
	                	//String filepath=".\\"+child.toString();
	                	//System.out.format("filepath %s", filepath);
	    				File file = child.toFile();
	    				if(!file.isDirectory())
	    				{
		                    byte[] content = new byte[(int)file.length()];
		    				BufferedInputStream input = new BufferedInputStream(new FileInputStream(file.getPath()));
		    				input.read(content);
		    				FileInfo fileif1 = new FileOp();
		                    fileif1.setInfo(filename, content);
		                    fs.PutFile(fileif1);
		    				input.close();
	    				}
					}
	                if (kind == ENTRY_DELETE) {
	                	File file = new File(filename);
	    				if(!file.isDirectory())
	    				{
	    					fs.DeleteFile(filename);
	    				}
					}
        		}
        		catch(Exception e) {
        			System.err.println("FileServer exception: "+ e.getMessage());
        			e.printStackTrace();
        		}

                if (loop && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    static void usage() {
        System.err.println("usage: java auto_detect dir hostname");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean loop = false;

        // register directory and process its events
        Path dir = Paths.get(args[0]);
        
		try
		{
			Registry registry = LocateRegistry.getRegistry(args[1]);
			String url = "//" + args[1] + "/FILE-SERVER";
            System.out.println(url);
			FileServer fs = (FileServer)registry.lookup("FILE-SERVER");

			System.out.println("Found remote server !");
			System.out.println("---------------------");
			new auto_detect(dir, loop, fs).processEvents();
		}
		catch(Exception e) {
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
		}
        
    }
}