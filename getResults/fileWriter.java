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
	/**
	 * @throws FileNotFoundException
	 * initialize the file
	 */
	public static void clear() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("index_path.txt");

		writer.close();
		
	}
	public static void writeln(String info, String fileName) throws Exception {
		FileWriter writer = new FileWriter(fileName, true);
        
        
        writer.write(info);
        writer.write("\r\n");
        writer.close();
	}
	/**
	 * @throws FileNotFoundException
	 * initialize the file
	 */
	public static void clear(String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);

		writer.close();
		
	}
}
