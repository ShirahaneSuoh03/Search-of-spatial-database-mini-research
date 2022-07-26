import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @author huangjunxiang
 * class to write the file
 */
public class fileWriter {
	/**
	 * @param info
	 * @throws Exception
	 * simply write but not add new line
	 */
	public static void write(String info) throws Exception {
		FileWriter writer = new FileWriter("index_path.txt", true);
        writer.write(info);
        writer.write(" ");
        
        writer.close();
	}
	public static void write(String info, String path) throws Exception {
		File file = new File(path);
		FileWriter writer = new FileWriter(file, true);
        writer.write(info);
        writer.write(" ");
        
        writer.close();
	}
	protected static void writelnUndu(String info, String path) throws Exception {
		FileWriter writer = new FileWriter(path, true);
        
        
        writer.write(info);
        writer.write("\r\n");
        writer.close();
	}
	/**
	 * @param info
	 * @throws Exception
	 * add a new line and then write
	 */
	public static void writeln(String info) throws Exception {
		FileWriter writer = new FileWriter("index_path.txt", true);
        
        writer.write("\r\n");
        writer.write(info);
        writer.close();
	}
	public static void writeln(String info,String path) throws Exception {
		File file = new File(path);
		FileWriter writer = new FileWriter(file, true);
        
        writer.write("\r\n");
        writer.write(info);
        writer.close();
	}
	/**
	 * @throws FileNotFoundException
	 * initialize the file
	 */
	public static void clear() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("index_path.txt");

		writer.close();
		
	}
	public static void clear(String path) throws FileNotFoundException {
		File file = new File(path);
		PrintWriter writer = new PrintWriter(file);

		writer.close();
		
	}
}
