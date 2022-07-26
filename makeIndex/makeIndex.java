import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class makeIndex {
	static PriorityQueue<SpatialRelation> linearScanList=new PriorityQueue<SpatialRelation>();
	public static ArrayList<SpatialRelation> distanceList;
	public static PriorityQueue<SpatialRelation> efficientScanList=new PriorityQueue<SpatialRelation>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static double numberGrid;
	public static ArrayList<SpatialRelation> SpRlist = new ArrayList<SpatialRelation>();
	public static HashMap<String, Set<SpatialRelation>> spatialSets = new HashMap<>();
	public static HashMap<String, ArrayList<String>> indexFile= new HashMap<>();
	// Remove duplicates using tree set
	public static ArrayList<SpatialRelation> spr_undu1 = new ArrayList<SpatialRelation>();
	public static TreeSet<SpatialRelation> spr_undu = new TreeSet<SpatialRelation>(new latComparator().thenComparing(new longComparator()));
	public static void duplicate_elimination(String data_path, String data_path_new) throws Exception{
		// read the original dataset from data_path
		// eliminate duplicates by deleting the corresponding lines
		// write the dataset without duplicates into data_path_new
		readFile(data_path);
		dataPreprocessing();
		
//		Collections.sort(SpRlist, new latComparator().thenComparing(new longComparator()));

		int c=0;
		for (SpatialRelation s : SpRlist) {
			spr_undu.add(s);
		}
//		System.out.println("undu "+spr_undu.size());
		for (SpatialRelation s : spr_undu) {
			c++;
			spr_undu1.add(s);
		}
		
//		System.out.println("undu1 "+c);
//		System.out.println("undu "+spr_undu.size());
		for(int i=0;i<spr_undu1.size();i++) {
			spr_undu.add(spr_undu1.get(i));
		}
//		System.out.println("undu "+spr_undu.size());
		fileWriter.clear(data_path_new);
		for(int i=0;i<spr_undu1.size();i++) {
			SpatialRelation sr=spr_undu1.get(i);
			String info=sr.getLatitude()+" "+sr.getLongitude()+" "+sr.getLocationid();
			fileWriter.writelnUndu(info,data_path_new);
		}

		
		
	}
	public static void create_index(String data_path_new, String index_path, int n) throws Exception{
		// To create a grid index and save it to file on "index_path".
		// The output file should contain exactly n*n lines. If there is no point in the cell, just leave it empty after ":".
		Iterator<SpatialRelation> iterator = spr_undu.iterator();

		//create hash map to store the spatial data using position

//		Scanner myInput = new Scanner( System.in );
//		System.out.print( "Enter integer: " );// ask user to enter how many boxes in a row/column
//		int n = myInput.nextInt();
		
		ArrayList<String> namelist = new ArrayList<String>();
		for(int i=0;i<n*n;i++) {
			//add the keys to the hash map
			Integer x_coor, y_coor;
			x_coor=i/n;
			y_coor=i%n;
			String indexHead = "c"+x_coor.toString()+","+y_coor.toString();
			namelist.add(indexHead);
			
			indexFile.put(indexHead, new ArrayList<String>());
		}

//		for(String name : namelist) {            
//		    spatialSets.put(name , new TreeSet<SpatialRelation>());
//		}
		//iterator of the treeset to generate the spatial index file
		while (iterator.hasNext()) {
			SpatialRelation temSR = iterator.next();
			double n2=(double) n;
			Integer idx=(int) ((temSR.getLatitude()-(-90.0))/(180.0/n2));
			Integer idy=(int) ((temSR.getLongitude()-(-176.3))/((176.3+177.5)/n2));
			if(idx>=n) {
				idx--;
			}
			else if(temSR.getLatitude()==-90.0+idx*(180.0/n2) && idx!= 0) {
				idx--;
			}
			if(idy>=n) {
				idy--;
			}
			else if(temSR.getLongitude()==-176.3+idy*((176.3+177.5)/n2) && idy!=0) {
				idy--;
			}
			
			ArrayList<String> temal=indexFile.get("c"+idx.toString()+","+idy.toString());
			temal.add(temSR.getLocationid()+"_"+temSR.getLatitude().toString()+"_"+temSR.getLongitude().toString());
		}
		writingIndex(n,index_path);
//		System.out.println(spr_undu.size());
//		System.out.println(SpRlist.size());
		
	}
	private static void readFile() throws IOException {
		String filename = "Gowalla_totalCheckins.txt";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		SpatialRelation s;
		while ( (line=br.readLine()) != null ) {
			s = processLine(line);
			if (s!=null) {
				SpRlist.add(s);
				
			}
		}
		br.close();
	}
	private static void readFile(String index) throws IOException {
		String filename = "Gowalla_totalCheckins.txt";
		File file=new File(index);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		SpatialRelation s;
		while ( (line=br.readLine()) != null ) {
			s = processLine(line);
			if (s!=null) {
				SpRlist.add(s);
				
			}
		}
		br.close();
	}
	private static SpatialRelation processLine(String line) {
		SpatialRelation spr = new SpatialRelation();

		String[] result = line.split("	");

		//if ( result.length != 7 ) return null;

		spr.setLocationid(result[4]);
		spr.setLatitude(Double.parseDouble(result[2]));
		spr.setLongitude(Double.parseDouble(result[3]));

		return spr;

	}
	private static void dataPreprocessing() {
		ArrayList<SpatialRelation> SpRlist_tem = new ArrayList<SpatialRelation>();
		// delete the data with invalid latitude or longitude
		for(SpatialRelation s : SpRlist) {
			if(s.getLatitude()==null || s.getLongitude()==null) {
				continue;
			}
			SpRlist_tem.add(s);
		}
		SpRlist=SpRlist_tem;
		SpRlist_tem = new ArrayList<SpatialRelation>();
		
		
	}
	private static void writingIndex(int n,String path) throws Exception {
		fileWriter.clear(path);
		fileWriter.write("*****************",path);
		for(int i=0;i<n*n;i++) {
			
			Integer x_coor, y_coor;
			x_coor=i/n;
			y_coor=i%n;
			String indexHead = "c"+x_coor.toString()+","+y_coor.toString();
			fileWriter.writeln(indexHead+": ",path);
			if(indexFile.get(indexHead)==null) {
				continue;
			}
			else {
				for(String s: indexFile.get(indexHead)) {
					fileWriter.write(s,path);
				}
			}
		}
	}
	public static void main(String[] args) throws Exception{
  		if(args.length != 4){
  			System.out.println("Usage: java makeIndex DATA_PATH INDEX_PATH DATA_PATH_NEW N");
  			/*
			DATA_PATH(String): the file path of Gowalla_totalCheckins.txt
			INDEX_PATH(String): the output file path of the grid index
			DATA_PATH_NEW(String): the file path of the dataset without duplicates
  			N(integer): the grid index size
			*/
  			return;
  		}
		duplicate_elimination(args[0], args[2]);
		long s = System.currentTimeMillis();
  		create_index(args[2], args[1], Integer.parseInt(args[3]));
		long t = System.currentTimeMillis();
		System.out.println("Index construction time: "+(t-s));
//		fileWriter.writeln(args[3]+","+(t-s),"/Users/huangjunxiang/eclipse-workspace/FITE3010Assignment2/loading_time.txt");
  	}
}	

class latComparator implements Comparator<SpatialRelation> {

	@Override
	public int compare(SpatialRelation o1, SpatialRelation o2) {
		// TODO Auto-generated method stub
		return o1.getLatitude().compareTo(o2.getLatitude());
	}
	
}

class longComparator implements Comparator<SpatialRelation> {

	@Override
	public int compare(SpatialRelation o1, SpatialRelation o2) {
		// TODO Auto-generated method stub
		return o1.getLongitude().compareTo(o2.getLongitude());
	}
	
}
