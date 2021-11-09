import java.util.*;

/**
 * 
 */

/**
 * @author huangjunxiang
 * class of the knn grid that stores various of information and relevant function
 */
public class knnGrid {
	private static HashMap<String, Set<SpatialRelation>> spatialSets = new HashMap<>();
	private static HashMap<String, Set<SpatialRelation>> spatialdistSets = new HashMap<>();
	/**
	 * @param s
	 * @param sr
	 */
	public static void addSpatialRelation(String s, SpatialRelation sr) {
		if(spatialSets.containsKey(s)) {
			spatialSets.get(s).add(sr);
		}
		else {
			spatialSets.put(s, new HashSet<SpatialRelation>());
			spatialSets.get(s).add(sr);
		}
	}
	/**
	 * @param s
	 * @param sr
	 */
	public static void addSpatialRelationDist(String s, SpatialRelation sr) {
		if(spatialSets.containsKey(s)) {
			spatialSets.get(s).add(sr);
		}
		else {
			spatialSets.put(s, new HashSet<SpatialRelation>());
			spatialSets.get(s).add(sr);
		}
	}
	/**
	 * print the size
	 */
	public static void showValue() {
		System.out.println(spatialSets.size());
	}
	/**
	 * @param s
	 * @return
	 */
	public static Set<SpatialRelation> getSpatialRelation(String s) {
		Set<SpatialRelation> sr=spatialSets.get(s);
		return sr;
	}
	/**
	 * @param s
	 * @return
	 */
	public static Set<SpatialRelation> getSpatialRelationDist(String s) {
		Set<SpatialRelation> sr=spatialSets.get(s);
		return sr;
	}
	/**
	 * @param s
	 * @return whether contain key s
	 */
	public static boolean containKeys(String s) {
		return spatialSets.containsKey(s);
	}
	public static void reset() {
		spatialdistSets=new HashMap<String, Set<SpatialRelation>>();
	}
}
