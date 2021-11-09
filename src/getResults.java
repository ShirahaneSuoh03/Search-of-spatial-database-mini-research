import com.sun.source.tree.Tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class getResults{
	static PriorityQueue<SpatialRelation> linearScanList=new PriorityQueue<SpatialRelation>();
	public static ArrayList<SpatialRelation> distanceList, efficientDistanceList;
	public static PriorityQueue<SpatialRelation> efficientScanList=new PriorityQueue<SpatialRelation>();
	public static TreeSet<SpatialRelation> diskScanList=new TreeSet<SpatialRelation>();
	public static HashMap<String, Set<SpatialRelation>> spatialSetsLimited = new HashMap<String, Set<SpatialRelation>>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static double numberGrid;
	public static ArrayList<SpatialRelation> SpRlist = new ArrayList<SpatialRelation>();
	public static HashMap<String, Set<SpatialRelation>> spatialSets = new HashMap<>();
	public static HashMap<String, ArrayList<String>> indexFile= new HashMap<>();
	public static ArrayList<SpatialRelation> unduplicatedData=new ArrayList<SpatialRelation>();

	/**
	 *
	 * @param s
	 * @param sr
	 */
	public static void addSpatialRelation(String s, SpatialRelation sr) {
		if(spatialSetsLimited.size()>8) spatialSetsLimited= new HashMap<String, Set<SpatialRelation>>();
		if(spatialSetsLimited.containsKey(s)) {
			spatialSetsLimited.get(s).add(sr);
		}
		else {
			spatialSetsLimited.put(s, new HashSet<SpatialRelation>());
			spatialSetsLimited.get(s).add(sr);
		}
	}

	/**
	 *
	 * @param s
	 * @return
	 */

	public static Set<SpatialRelation> getSpatialRelation(String s) {
		Set<SpatialRelation> sr=spatialSetsLimited.get(s);
		return sr;
	}

	/**
	 *
	 * @param qx
	 * @param qy
	 * @param index_path
	 * @param k
	 * @param n
	 * @return
	 * @throws IOException
	 */

	public static String knn_grid(double qx, double qy, String index_path, int k, int n) throws IOException{
		// to get the k-NN result with the help of the grid index
		// Please store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"
//		knnGrid.reset();
//		long s = System.currentTimeMillis();		
//		fileReader.readFile();
//		long e = System.currentTimeMillis();
//		System.out.println("Linear scan time: "+(e-s));

		Integer xid=(int) ((qx-(-90.0))/(180.0/n));
		Integer yid=(int) ((qy-(-176.3))/((176.3+177.5)/n));
		String grid="c"+xid.toString()+","+yid.toString();

		double t=Double.MAX_VALUE;//kth largest distance
		distanceList=new ArrayList<SpatialRelation>();


		Set<SpatialRelation> ssr=knnGrid.getSpatialRelation(grid);//problem with this maybe the key is not properly set
		
		if(ssr==null) {
			
		}
		else {
			Iterator<SpatialRelation> iterator = ssr.iterator();
			while (iterator.hasNext()) {//iterate through the set of the pointed grid.
				SpatialRelation temSR = iterator.next();
				double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
				temSR.setDistance(dis);
				distanceList.add(temSR);
				
				
			}

			Collections.sort(distanceList);
			if(distanceList.size()>=k) {//update the value of t
				t=distanceList.get(k-1).getDistance();
			}
			else {
				t=Double.MAX_VALUE;;
			}
		}
		efficientScanList=new PriorityQueue<SpatialRelation>();
		for(SpatialRelation sr : distanceList) efficientScanList.add(sr);
		System.out.println("knn1: "+efficientScanList.size());//test
		int layer=1;
		boolean indicator=true;
		int count=1;
		while(indicator){
			indicator=false;
			for(int i=-1*layer;i<=1*layer;i++) {
				for(int j=-1*layer;j<=1*layer;j++) {//search layer, compute the dlow. check the grid only when dlow satisfy the condition
					if(i==0 && j==0) continue;
					if((((i==1*layer || i==-1*layer) && (j!=0)) || ((j==-1*layer || j==1*layer) && (i!=0)))) {
						double x,y;

						double dlow;
						x=-90+(180.0/n)*xid;//This should not be xid
						y=-176.3+((177.5+176.3)/n)*yid;
						dlow=calEuclideanDistance(x,y,qx,qy);
						if(dlow>calEuclideanDistance(x+(180.0/n),y,qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y,qx,qy);
						}
						if(dlow>calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n) ) {
								count++;
								indicator=true;
								double tlayer=checkGrid(t,k,xid+i,yid+j,qx,qy);

								if(tlayer==0) continue;
								t=tlayer;


							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}

						}
					}
					else if(i==0) {
						double dlow;
						double y=-176.3+((177.5+176.3)/n)*yid;
						if(j==1*layer) {
							dlow=Math.abs(qy-(y+((177.5+176.3)/n)));
						}
						else if(j==-1*layer) {
							dlow=Math.abs(qy-y);
						}
						else {
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n)) {

								count++;
								indicator=true;
								double tlayer=checkGrid(t,k,xid+i,yid+j,qx,qy);
								if(tlayer==0) continue;
								t=tlayer;
							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}
						}

					}
					else if(j==0) {
						double dlow;
						double x=-90+(180.0/n)*xid;
						if(i==1*layer) {
							dlow=Math.abs(qx-(x+((177.5+176.3)/n)));
						}
						else if(i==-1*layer){
							dlow=Math.abs(qx-x);
						}
						else{
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n)) {
								count++;
								indicator=true;
								double tlayer=checkGrid(t,k,xid+i,yid+j,qx,qy);
								if(tlayer==0) continue;
								t=tlayer;

							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}
						}
					}


				}
			}
			layer++;
			if(count==n*n) break;
		}

//		System.out.println("knn2: "+efficientScanList.size());//test
		String result="";
		if(k>efficientScanList.size()) k=efficientScanList.size();
		for(int i=0;i<k;i++) {
			if(i==k-1){
				result+=efficientScanList.poll().getLocationid();
				break;
			}
			result+=efficientScanList.poll().getLocationid()+", ";
		}
		return result;
		
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param data_path_new
	 * @param k
	 * @return
	 * @throws IOException
	 */
	public static String knn_linear_scan(double x, double y, String data_path_new, int k) throws IOException{
		// to get the k-NN result by linear scan
		// Please store the k-NN results by a String of location ids, like "11, 789, 125, 2, 771"
  		//This is the beginning part of search part
//			fileReader.readFile();
//			knnGrid.showValue();
//			double gn=fileReader.getGridNumber();
//			numberGrid=fileReader.getGridNumber();
		SpRlist=new ArrayList<SpatialRelation>();
		linearScanList=new PriorityQueue<SpatialRelation>();
		unduplicatedData=new ArrayList<SpatialRelation>();
		readFile(data_path_new);//to write a new read file

		TreeSet<SpatialRelation> spr_undu = new TreeSet<SpatialRelation>(new distComparator());
		for (SpatialRelation s : unduplicatedData) {
			s.setDistance(calEuclideanDistance(x,y,s.getLatitude(),s.getLongitude()));
			spr_undu.add(s);
		}


		String result="";
		for(int i=0;i<k;i++) {
			SpatialRelation temp=spr_undu.pollFirst();
//			System.out.println(temp.getDistance());
			if(i==k-1){
				result+=temp.getLocationid();
				break;
			}
			result+=temp.getLocationid()+", ";
		}
		return result;
	}

	/**
	 *
	 * @throws IOException
	 */
	public static void readFile() throws IOException {
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

	/**
	 *
	 * @param path
	 * @throws IOException
	 */
	public static void readFile(String path) throws IOException {
		File file=new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		SpatialRelation s;
		while ( (line=br.readLine()) != null ) {
			s = processLineUnduplicated(line);
			if (s!=null) {
				unduplicatedData.add(s);

			}
		}
		br.close();
	}

	private static SpatialRelation processLineUnduplicated(String line) {
		SpatialRelation spr = new SpatialRelation();

		String[] result = line.split(" ");

		//if ( result.length != 7 ) return null;
		if(result.length<3){
			return null;
		}
		spr.setLocationid(result[2]);
		spr.setLatitude(Double.parseDouble(result[0]));
		spr.setLongitude(Double.parseDouble(result[1]));

		return spr;

	}

	/**
	 * @param x latitude
	 * @param y longitude
	 * @param ns number of grids
	 * @return the spatial query
	 */
	public static SpatialQuery knn_grid_query(double x, double y, double ns) {
		int n=(int)Math.sqrt(ns);
		
		Integer idx=(int) ((x-(-90.0))/(180.0/n));
		Integer idy=(int) ((y-(-176.3))/((176.3+177.5)/n));
//		System.out.println(idx+" "+idy+" "+ns);
		SpatialQuery sq=new SpatialQuery(idx,idy,x,y);
		return sq;
	}
	

	
		
	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double calEuclideanDistance(double x1, double y1, double x2, double y2) {
		double dis=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
		return dis;
	}
	/**
	 * @param t
	 * @param k
	 * @param xid
	 * @param yid
	 * @param qx
	 * @param qy
	 * @return
	 * check the entry in the grid whether they satisfied the distance requirement.
	 */
	private static double checkGrid(double t, int k, Integer xid, Integer yid,  double qx, double qy) {
		String grid="c"+xid.toString()+","+yid.toString();
		Set<SpatialRelation> ssr=knnGrid.getSpatialRelation(grid);
		if(ssr==null) {
			return 0;
		}

		Iterator<SpatialRelation> iterator = ssr.iterator();


		while (iterator.hasNext()) {//iterate through the set of the pointed grid.
			SpatialRelation temSR = iterator.next();

			double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
			temSR.setDistance(dis);
			efficientScanList.add(temSR);

			

		}
		PriorityQueue<SpatialRelation> tem = new PriorityQueue<SpatialRelation>();
		if(efficientScanList.size()>=k) {//update the value of t
			for(int i=0;i<k;i++) {
				SpatialRelation spatialRelation = efficientScanList.poll();
				tem.add(spatialRelation);
				if (i == k - 1) {
					t = spatialRelation.getDistance();
				}
			}
			for(int i=0;i<k;i++){
				efficientScanList.add(tem.poll());
			}
		}
		else {
//			tem = new PriorityQueue<SpatialRelation>();
//			int j=efficientScanList.size();
//			for(int i=0;i<j;i++){
//				SpatialRelation spatialRelation=efficientScanList.poll();
//				tem.add(spatialRelation);
//				if(i==j-1){
//					t=spatialRelation.getDistance();
//				}
//			}
//			for(int i=0;i<j;i++){
//				efficientScanList.add(tem.poll());
//			}
			t=Double.MAX_VALUE;
		}
		return t;
	}

	private static double checkGridDisk(double t, int k, Integer xid, Integer yid,  double qx, double qy) throws IOException {
		String grid="c"+xid.toString()+","+yid.toString();
		fileReader.readFileDisk(grid);
		Set<SpatialRelation> ssr=getSpatialRelation(grid);
		if(ssr==null) {
			return 0;
		}

		Iterator<SpatialRelation> iterator = ssr.iterator();


		while (iterator.hasNext()) {//iterate through the set of the pointed grid.
			SpatialRelation temSR = iterator.next();

			double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
			temSR.setDistance(dis);
			diskScanList.add(temSR);



		}
		PriorityQueue<SpatialRelation> tem = new PriorityQueue<SpatialRelation>();
		if(diskScanList.size()>=k) {//update the value of t
			for(int i=0;i<k;i++) {
				SpatialRelation spatialRelation = diskScanList.pollFirst();
				tem.add(spatialRelation);
				if (i == k - 1) {
					t = spatialRelation.getDistance();
				}
			}
			for(int i=0;i<k;i++){
				diskScanList.add(tem.poll());
			}
		}
		else {
//			tem = new PriorityQueue<SpatialRelation>();
//			int j=diskScanList.size();
//			for(int i=0;i<j;i++){
//				SpatialRelation spatialRelation=diskScanList.pollFirst();
//				tem.add(spatialRelation);
//				if(i==j-1){
//					t=spatialRelation.getDistance();
//				}
//			}
//			for(int i=0;i<j;i++){
//				diskScanList.add(tem.poll());
//			}
			t=Double.MAX_VALUE;
		}
		return t;
	}

	/**
	 * This algorithm is intended to use the MBR of the query with radius of t, to further improve the knn grid algorithm,
	 * it could prune some entries in the qualified grid, and do not calculate euclidean distance for them.
	 */
	private static double efficientCheckGrid(double t, int k, Integer xid, Integer yid,  double qx, double qy) {
		String grid="c"+xid.toString()+","+yid.toString();
		Set<SpatialRelation> ssr=knnGrid.getSpatialRelation(grid);
		if(ssr==null) {
			return 0;
		}
		double mbrxLower=qx-t;
		double mbrxUpper=qx+t;
		double mbryLower=qy-t;
		double mbryUpper=qy+t;
		Iterator<SpatialRelation> iterator = ssr.iterator();
		

		while (iterator.hasNext()) {//iterate through the set of the pointed grid.
			SpatialRelation temSR = iterator.next();
			if(efficientScanList.size()==k){
				PriorityQueue<SpatialRelation> temp = new PriorityQueue<SpatialRelation>();
				for(int i=0;i<k;i++) {
					SpatialRelation spatialRelation = efficientScanList.poll();
					temp.add(spatialRelation);
					if (i == k - 1) {
						t = spatialRelation.getDistance();
					}
				}
				for(int i=0;i<k;i++){
					efficientScanList.add(temp.poll());
				}
				mbrxLower=qx-t;
				mbrxUpper=qx+t;
				mbryLower=qy-t;
				mbryUpper=qy+t;
			}

			if(efficientScanList.size()>=k) {
				if(temSR.getLatitude()>mbrxUpper || temSR.getLatitude()<mbrxLower || temSR.getLongitude()>mbryUpper || temSR.getLongitude()<mbryLower) {
					continue;
				}
				
			}
			double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
			temSR.setDistance(dis);
			efficientScanList.add(temSR);

			
			
		}
		PriorityQueue<SpatialRelation> tem = new PriorityQueue<SpatialRelation>();
		if(efficientScanList.size()>=k) {//update the value of t
			for(int i=0;i<k;i++) {
				SpatialRelation spatialRelation = efficientScanList.poll();
				tem.add(spatialRelation);
				if (i == k - 1) {
					t = spatialRelation.getDistance();
				}
			}
			for(int i=0;i<k;i++){
				efficientScanList.add(tem.poll());
			}
		}
		else {
//			tem = new PriorityQueue<SpatialRelation>();
//			int j=efficientScanList.size();
//			for(int i=0;i<j;i++){
//				SpatialRelation spatialRelation=efficientScanList.poll();
//				tem.add(spatialRelation);
//				if(i==j-1){
//					t=spatialRelation.getDistance();
//				}
//			}
//			for(int i=0;i<j;i++){
//				efficientScanList.add(tem.poll());
//			}
			t=Double.MAX_VALUE;
		}

		return t;
	}

	private static String knn_grid_disk(double qx, double qy, String index_path, int k, int n) throws IOException{ //1. only store distance and id 2. only keep 10 element in data structure
		//suppose that we only have buffer of 8 blocks
		Integer xid=(int) ((qx-(-90.0))/(180.0/n));
		Integer yid=(int) ((qy-(-176.3))/((176.3+177.5)/n));
		String grid="c"+xid.toString()+","+yid.toString();
		spatialSetsLimited=new HashMap<String, Set<SpatialRelation>>();
		double t=Double.MAX_VALUE;//kth largest distance
		distanceList=new ArrayList<SpatialRelation>();

//		if(spatialSetsLimited.size()>8) spatialSetsLimited=new HashMap<String, Set<SpatialRelation>>();//AUTOMATICALLY clear the current memory buffer if the total blocks exceed 8

		fileReader.readFileDisk(grid);//get the specified cell

		//if there are exceeded amount, throw the oldest one to the garbage collector
		//use queue to store the information



		Set<SpatialRelation> ssr=getSpatialRelation(grid);//problem with this maybe the key is not properly set

		if(ssr==null) {

		}
		else {
			Iterator<SpatialRelation> iterator = ssr.iterator();
			while (iterator.hasNext()) {//iterate through the set of the pointed grid.
				SpatialRelation temSR = iterator.next();
				double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
				temSR.setDistance(dis);
				distanceList.add(temSR);


			}

			Collections.sort(distanceList);
			if(distanceList.size()>=k) {//update the value of t
				t=distanceList.get(k-1).getDistance();
			}
			else {
				t=Double.MAX_VALUE;
			}
		}
		diskScanList=new TreeSet<SpatialRelation>();
		for(SpatialRelation sr : distanceList) diskScanList.add(sr);
//		System.out.println("Disk1: "+diskScanList.size());//test
		int layer=1;
		boolean indicator=true;
		int count=1;
		while(indicator){
			indicator=false;
			for(int i=-1*layer;i<=1*layer;i++) {
				for(int j=-1*layer;j<=1*layer;j++) {//search layer, compute the dlow. check the grid only when dlow satisfy the condition
					if(i==0 && j==0) continue;
					if((((i==1*layer || i==-1*layer) && (j!=0)) || ((j==-1*layer || j==1*layer) && (i!=0)))) {
						double x,y;

						double dlow;
						x=-90+(180.0/n)*xid;
						y=-176.3+((177.5+176.3)/n)*yid;
						dlow=calEuclideanDistance(x,y,qx,qy);
						if(dlow>calEuclideanDistance(x+(180.0/n),y,qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y,qx,qy);
						}
						if(dlow>calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n) ) {
								count++;
								indicator=true;
								double tlayer=checkGridDisk(t,k,xid+i,yid+j,qx,qy);

								if(tlayer==0) continue;
								t=tlayer;


							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}

						}
					}
					else if(i==0) {
						double dlow;
						double y=-176.3+((177.5+176.3)/n)*yid;
						if(j==1*layer) {
							dlow=Math.abs(qy-(y+((177.5+176.3)/n)));
						}
						else if(j==-1*layer) {
							dlow=Math.abs(qy-y);
						}
						else {
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n)) {

								count++;
								indicator=true;
								double tlayer=checkGridDisk(t,k,xid+i,yid+j,qx,qy);
								if(tlayer==0) continue;
								t=tlayer;
							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}
						}

					}
					else if(j==0) {
						double dlow;
						double x=-90+(180.0/n)*xid;
						if(i==1*layer) {
							dlow=Math.abs(qx-(x+((177.5+176.3)/n)));
						}
						else if(i==-1*layer){
							dlow=Math.abs(qx-x);
						}
						else{
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if((xid+i>=0 && xid+i<n && yid+j>=0 && yid+j<n)) {
								count++;
								indicator=true;
								double tlayer=checkGridDisk(t,k,xid+i,yid+j,qx,qy);
								if(tlayer==0) continue;
								t=tlayer;

							}
							else if(efficientScanList.size()<10){
								indicator=true;
							}
						}
					}


				}
			}
			layer++;
			if(count==n*n) break;
		}

//		System.out.println("Disk2: "+diskScanList.size());//test
		String result="";
		if(k>diskScanList.size()) k=diskScanList.size();
		for(int i=0;i<k;i++) {
			if(i==k-1){
				result+=diskScanList.pollFirst().getLocationid();
				break;
			}
			result+=diskScanList.pollFirst().getLocationid()+", ";
		}
		return result;

	}

	private static String knn_grid_fast(double qx, double qy, String index_path, int k, int n) throws IOException {
//		knnGrid.reset();
//		fileReader.readFile();
		Integer xid=(int) ((qx-(-90.0))/(180.0/n));
		Integer yid=(int) ((qy-(-176.3))/((176.3+177.5)/n));
		String grid="c"+xid.toString()+","+yid.toString();

		double t=Double.MAX_VALUE;//kth largest distance
		distanceList=new ArrayList<SpatialRelation>();


		Set<SpatialRelation> ssr=knnGrid.getSpatialRelation(grid);//problem with this maybe the key is not properly set

		if(ssr==null) {

		}
		else {
			Iterator<SpatialRelation> iterator = ssr.iterator();
			while (iterator.hasNext()) {//iterate through the set of the pointed grid.
				SpatialRelation temSR = iterator.next();
				double dis=calEuclideanDistance(temSR.getLatitude(),temSR.getLongitude(),qx,qy);
				temSR.setDistance(dis);
				distanceList.add(temSR);


			}

			Collections.sort(distanceList);
			if(distanceList.size()>=k) {//update the value of t
				t=distanceList.get(k-1).getDistance();
			}
			else {
				t=Double.MAX_VALUE;
			}
		}
		efficientScanList=new PriorityQueue<SpatialRelation>();
		for(SpatialRelation sr : distanceList) efficientScanList.add(sr);
//		System.out.println("fast1: "+efficientScanList.size());//test
		int layer=1;
		boolean indicator=true;
		int count=1;
		while(indicator){
			indicator=false;
			for(int i=-1*layer;i<=1*layer;i++) {
				for(int j=-1*layer;j<=1*layer;j++) {//search layer, compute the dlow. check the grid only when dlow satisfy the condition
					if(i==0 && j==0) continue;
					if((((i==1*layer || i==-1*layer) && (j!=0)) || ((j==-1*layer || j==1*layer) && (i!=0)))) {
						double x,y;

						double dlow;
						x=-90+(180.0/n)*xid;
						y=-176.3+((177.5+176.3)/n)*yid;
						dlow=calEuclideanDistance(x,y,qx,qy);
						if(dlow>calEuclideanDistance(x+(180.0/n),y,qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y,qx,qy);
						}
						if(dlow>calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x,y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy)) {
							dlow=calEuclideanDistance(x+(180.0/n),y+((177.5+176.3)/n),qx,qy);
						}
						if(dlow>t) {
							continue;
						}
						else {
							if(!(xid+i>=0 && xid+i<=n && yid+j>=0 && yid+j<=n)) continue;
							count++;
							indicator=true;
							double tlayer=efficientCheckGrid(t,k,xid+i,yid+j,qx,qy);

							if(tlayer==0) continue;
							t=tlayer;
						}
					}
					else if(i==0) {
						double dlow;
						double y=-176.3+((177.5+176.3)/n)*yid;
						if(j==1*layer) {
							dlow=Math.abs(qy-(y+((177.5+176.3)/n)));
						}
						else if(j==-1*layer) {
							dlow=Math.abs(qy-y);
						}
						else {
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if(!(xid+i>=0 && xid+i<=n && yid+j>=0 && yid+j<=n)) continue;
							count++;
							indicator=true;
							double tlayer=efficientCheckGrid(t,k,xid+i,yid+j,qx,qy);
							if(tlayer==0) continue;
							t=tlayer;
						}

					}
					else if(j==0) {
						double dlow;
						double x=-90+(180.0/n)*xid;
						if(i==1*layer) {
							dlow=Math.abs(qx-(x+((177.5+176.3)/n)));
						}
						else if(i==-1*layer){
							dlow=Math.abs(qx-x);
						}
						else{
							continue;
						}
						if(dlow>t) {
							continue;
						}
						else {
							if(!(xid+i>=0 && xid+i<=n && yid+j>=0 && yid+j<=n)) continue;
							count++;
							indicator=true;
							double tlayer=efficientCheckGrid(t,k,xid+i,yid+j,qx,qy);
							if(tlayer==0) continue;
							t=tlayer;
						}
					}


				}
			}
			layer++;
			if(count==n*n) break;
		}

//		System.out.println("fast2: "+efficientScanList.size());//test
		String result="";
		if(k>efficientScanList.size()) k=efficientScanList.size();
		for(int i=0;i<k;i++) {
			if(i==k-1){
				result+=efficientScanList.poll().getLocationid();
				break;
			}
			result+=efficientScanList.poll().getLocationid()+", ";
		}
		return result;

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

	/**
	 *
	 * @param str
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @throws Exception
	 */
	public static void testMachine(String str, String arg2, String arg3, Integer arg4, Integer arg5) throws Exception {
		Random rand = new Random();
		ArrayList<Double> xran=new ArrayList<Double>();
		ArrayList<Double> yran=new ArrayList<Double>();
		ArrayList<Long> avg=new ArrayList<Long>();
		for(int i=0;i<100;i++) {
			double x_random= rand.nextDouble()*(180.0)-90.0;
			double y_random= rand.nextDouble()*((176.3+177.5))-176.3;
			xran.add(x_random);
			yran.add(y_random);
		}
		
		long s,t;
		double x,y;
		for(int i=0;i<100;i++) {
			if(str.equals("knn")) {
				x=xran.get(i);
				y=yran.get(i);
				s = System.currentTimeMillis();
				System.out.println(i+": "+"Grid index search results: "+knn_grid(x, y, arg3, arg4, arg5));

				t = System.currentTimeMillis();
				avg.add((t-s));
				
			}
			else if(str.equals("efficient")) {
				x=xran.get(i);
				y=yran.get(i);
				s = System.currentTimeMillis();
				System.out.println(i+": "+"Efficient grid index search results: "+knn_grid_fast(x, y, arg3, arg4, arg5));

				t = System.currentTimeMillis();
				avg.add((t-s));
			}
			else if(str.equals("linear")) {
				x=xran.get(i);
				y=yran.get(i);
				s = System.currentTimeMillis();
				System.out.println(i+": "+"linear search results: "+knn_linear_scan(x, y, arg2, arg4));

				t = System.currentTimeMillis();
				avg.add((t-s));

			}
			else if(str.equals("disk")) {
				x=xran.get(i);
				y=yran.get(i);
				s = System.currentTimeMillis();
				System.out.println(i+": "+"knn grid disk results: "+knn_grid_disk(x, y, arg3, arg4, arg5));

				t = System.currentTimeMillis();
				avg.add((t-s));

			}
			else if(str.equals("RTree")) {
				x=xran.get(i);
				y=yran.get(i);
				s = System.currentTimeMillis();
				System.out.println(i+": "+"R Tree search results: "+RTree.searchRTree(x, y, arg3, arg4, arg5));

				t = System.currentTimeMillis();
				avg.add((t-s));

			}
		}
		Long average=0L;
		for(int i=0;i<100;i++) {
			average+=avg.get(i);
		}
		average/=100;
//		fileWriter.writeln(arg5.toString()+","+average.toString(), "avg_execution_time_linear_n.txt");
		System.out.println(arg5.toString()+","+average.toString());
	}

	/**
	 *
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @throws Exception
	 */
	public static void testMachine(String arg2, String arg3, Integer arg4, Integer arg5) throws Exception {
		Random rand = new Random();
		ArrayList<Double> xran=new ArrayList<Double>();
		ArrayList<Double> yran=new ArrayList<Double>();
		ArrayList<Long> avg=new ArrayList<Long>();
		ArrayList<Long> avg1=new ArrayList<Long>();
		ArrayList<Long> avg2=new ArrayList<Long>();
		for(int i=0;i<100;i++) {
			double x_random= rand.nextDouble()*(180.0)-90.0;
			double y_random= rand.nextDouble()*((176.3+177.5))-176.3;
			xran.add(x_random);
			yran.add(y_random);
		}

		long s,t;
		double x,y;
		boolean bl=true;
		String knn,fast,disk,linear;
		for(int i=0;i<100;i++) {



			x=xran.get(i);
			y=yran.get(i);
			s = System.currentTimeMillis();
			knn=knn_grid(x, y, arg3, arg4, arg5);
			System.out.println(i+": "+"Grid index search results: "+knn);
			t = System.currentTimeMillis();
			avg.add((t-s));

			x=xran.get(i);
			y=yran.get(i);
			s = System.currentTimeMillis();
			fast=knn_grid_fast(x, y, arg3, arg4, arg5);
			System.out.println(i+": "+"Efficient grid index search results: "+fast);

			t = System.currentTimeMillis();
			avg1.add((t-s));

//			x=xran.get(i);
//			y=yran.get(i);
//			s = System.currentTimeMillis();
//			disk=knn_grid_disk(x, y, arg3, arg4, arg5);
//			System.out.println(i+": "+"disk grid index search results: "+disk);
//			System.out.println(knn.equals(disk));
//			t = System.currentTimeMillis();
//			avg2.add((t-s));

			x=xran.get(i);
			y=yran.get(i);
			s = System.currentTimeMillis();
			linear=knn_linear_scan(x, y, arg2, arg4);
			System.out.println(i+": "+"linear search results: "+linear);
			System.out.println(knn.equals(linear));
			t = System.currentTimeMillis();
			avg2.add((t-s));

//			x=xran.get(i);
//			y=yran.get(i);
//			s = System.currentTimeMillis();
//			String Rtree=RTree.searchRTree(x, y, arg3, arg4, arg5);
//			System.out.println(i+"R-TREE search results: "+Rtree);
//			t = System.currentTimeMillis();
//			System.out.println(x+" "+y);
//			System.out.println(knn.equals(Rtree));
//			System.out.println("R-TREE search time: "+(t-s));
//			avg2.add(t-s);


			if((knn.equals(linear))){

			}
			else{
				bl=false;
			}


		}
		Long average=0L;
		for(int i=0;i<100;i++) {
			average+=avg.get(i);
		}
		average/=100;
//		fileWriter.writeln(arg5.toString()+","+average.toString(), "avg_execution_time_n.txt");
		System.out.println(arg5.toString()+","+average.toString());

		average=0L;
		for(int j=0;j<100;j++) {
			average+=avg1.get(j);
		}
		average/=100;
		System.out.println(arg5.toString()+","+average.toString());
//		fileWriter.writeln(arg5.toString()+","+average.toString(), "avg_execution_time_efficient_n.txt");

		average=0L;
		for(int j=0;j<100;j++) {
			average+=avg2.get(j);
		}
		average/=100;
//		fileWriter.writeln(arg4.toString()+","+average.toString(), "avg_execution_time_disk_k.txt");
		System.out.println(arg5.toString()+","+average.toString());
		System.out.println(bl);

	}

	public static void main(String args[]) throws Exception{
  		if(args.length != 6){
  			System.out.println("Usage: java getResults X Y DATA_PATH_NEW INDEX_PATH K N");
  			/*
			X(double): the latitude of the query point q
			Y(double): the longitude of the query point q
			DATA_PATH_NEW(String): the file path of dataset you generated without duplicates
			INDEX_PATH(String): the file path of the grid index
			K(integer): the k value for k-NN search
			N(integer): the grid index size
  			*/



  			return;
  		}

		  RTree.bulkLoading();
		  long s = System.currentTimeMillis();
		  System.out.println("Linear scan results: "+knn_linear_scan(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[2], Integer.parseInt(args[4])));
		long t = System.currentTimeMillis();
		System.out.println("Linear scan time: "+(t-s));
		
		s = System.currentTimeMillis();
		fileReader.readFile(args[3]);
		t = System.currentTimeMillis();
//		fileWriter.clear("Part3_loading_time.txt");
		fileWriter.writeln((args[5]+","+(t-s)), "Part3_loading_time.txt");
		
		s = System.currentTimeMillis();
  		System.out.println("Grid index search results: "+knn_grid(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5])));
		t = System.currentTimeMillis();
		System.out.println("Grid index search time: "+(t-s));
		s = System.currentTimeMillis();
  		System.out.println("Efficient grid index search results: "+knn_grid_fast(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5])));
		t = System.currentTimeMillis();
		System.out.println("Grid index search time: "+(t-s));

		s = System.currentTimeMillis();
		System.out.println("R-TREE search results: "+RTree.searchRTree(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5])));
		t = System.currentTimeMillis();
		System.out.println("R-TREE search time: "+(t-s));
		Scanner scanner=new Scanner(System.in);
		String answer;
		System.out.println("1: Test all (input 1)");
		System.out.println("2: input the method (knn, linear, efficient, disk, RTree)");
		System.out.print("Select mode: ");
		answer=scanner.nextLine();

		if(answer.equals("1")){
			testMachine(args[2],args[3],Integer.parseInt(args[4]),Integer.parseInt(args[5]));
		}
		else {
			testMachine(answer,args[2],args[3],Integer.parseInt(args[4]),Integer.parseInt(args[5]));
		}






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
class distComparator implements Comparator<SpatialRelation> {

	@Override
	public int compare(SpatialRelation o1, SpatialRelation o2) {
		// TODO Auto-generated method stub
		return o1.getDistance().compareTo(o2.getDistance());
	}
	
}