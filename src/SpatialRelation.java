import java.util.Comparator;

/**
 * 
 */

/**
 * @author huangjunxiang
 * The class for tbe spatial relation, contains relevant attribute and functions.
 */
public class SpatialRelation implements Comparable<SpatialRelation> {
	private Double latitude, longitude;
	private Double distance;
	public Double xlower,xupper,ylower,yupper;
	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}


	private String locationid;
	
	/**
	 * print the spatial relation info
	 */
	public void printRelation(){
		System.out.println(latitude.toString()+" "+longitude.toString()+" "+locationid+" "+distance);
	}
	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		if(latitude<=90 && latitude>=-90) {
			this.latitude = latitude;	
		}
	}
	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		if(longitude<=177.5 && longitude>=-176.3) {
			this.longitude = longitude;
		}
		
	}
	/**
	 * @return the locationid
	 */
	public String getLocationid() {
		return locationid;
	}
	/**
	 * @param locationid the locationid to set
	 */
	public void setLocationid(String locationid) {
		this.locationid = locationid;
	}



	@Override
	public int compareTo(SpatialRelation o) {
		// TODO Auto-generated method stub
		return this.distance.compareTo(o.distance);
	}
}
