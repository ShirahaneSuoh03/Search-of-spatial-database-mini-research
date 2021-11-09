import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author huangjunxiang
 *
 */
public class fileReader {

	public static double count = 0;

	public static void readFile() throws IOException {
		count = 0;
		String filename = "index_path.txt";

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		ArrayList<gridInfo> s;
		while ((line = br.readLine()) != null) {
			s = processLine(line);

			if (s != null) {//add the spatial relation to hash map

				for (int i = 0; i < s.size(); i++) {
					gridInfo sr = s.get(i);

					knnGrid.addSpatialRelation(sr.gridID, sr);
				}

			}
		}
		br.close();
	}
	public static void readFile(String index) throws IOException {
		count = 0;

		File file=new File(index);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		ArrayList<gridInfo> s;
		while ((line = br.readLine()) != null) {
			s = processLine(line);

			if (s != null) {//add the spatial relation to hash map

				for (int i = 0; i < s.size(); i++) {
					gridInfo sr = s.get(i);

					knnGrid.addSpatialRelation(sr.gridID, sr);
				}

			}
		}
		br.close();
	}
	/**
	 * @param line
	 * @return
	 */
	private static ArrayList<gridInfo> processLine(String line) {

		if (line.equals("***************** ")) {

			return null;
		}
		count++;
		String[] result = line.split(" ");


		String s = result[0].substring(0, result[0].length() - 1);
//		System.out.println(s);//test only
		ArrayList<gridInfo> spr = new ArrayList<gridInfo>();
		int c = 0;//for test only
		for (int i = 1; i < result.length; i++) {
			gridInfo gi = new gridInfo();
			gi.gridID = s;
			String[] info = result[i].split("_");
			if (info.length != 3) {
				System.out.println("skip");
				continue;
			}
			gi.setLocationid(info[0]);

			gi.setLatitude(Double.parseDouble(info[1]));
			gi.setLongitude(Double.parseDouble(info[2]));
			spr.add(gi);

		}
		//for test only
		if (result.length <= 1) {

			return null;
		}

		return spr;

	}

	public static double getGridNumber() {

		return count;
	}

	public static void readFileDisk(Integer xid, Integer yid) throws IOException {

		String filename = "index_path.txt";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		ArrayList<gridInfo> s;
		while ((line = br.readLine()) != null) {
			String[] lineSplitted = line.split(" ");
			if (lineSplitted[0].equals("c" + xid.toString() + "," + yid.toString() + ":")) {
				s = processLineDisk(line);

				if (s != null) {//add the spatial relation to hash map

					for (int i = 0; i < s.size(); i++) {
						gridInfo sr = s.get(i);

						getResults.addSpatialRelation(sr.gridID, sr);
					}
					br.close();
					break;
				}

			}

		}
		br.close();
	}

	public static void readFileDisk(String grid) throws IOException {


		String filename = "index_path.txt";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		ArrayList<gridInfo> s;
		while ((line = br.readLine()) != null) {
			if (line.split(" ")[0].substring(0, line.split(" ")[0].length() - 1).equals(grid)) {
				s = processLine(line);
			} else {
				continue;
			}


			if (s != null) {//add the spatial relation to hash map

				for (int i = 0; i < s.size(); i++) {
					gridInfo sr = s.get(i);

					getResults.addSpatialRelation(sr.gridID, sr);
				}
				br.close();
				break;
			}
		}
		br.close();
	}

	public static ArrayList<gridInfo> processLineDisk(String line) {


		if (line.equals("***************** ")) {

			return null;
		}

		String[] result = line.split(" ");


		String s = result[0].substring(0, result[0].length() - 1);
//		System.out.println(s);//test only
		ArrayList<gridInfo> spr = new ArrayList<gridInfo>();
		int c = 0;//for test only
		for (int i = 1; i < result.length; i++) {
			gridInfo gi = new gridInfo();
			gi.gridID = s;
			String[] info = result[i].split("_");
			if (info.length != 3) {
				System.out.println("skip");
				continue;
			}
			gi.setLocationid(info[0]);

			gi.setLatitude(Double.parseDouble(info[1]));
			gi.setLongitude(Double.parseDouble(info[2]));
			spr.add(gi);

		}
		//for test only
		if (result.length <= 1) {

			return null;
		}

		return spr;
	}
}