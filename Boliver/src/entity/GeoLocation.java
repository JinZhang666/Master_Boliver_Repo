package entity;

public class GeoLocation {
	private final double lat;
	private final double lon;
	private final String address;

	public GeoLocation(double lat, double lon, String address) {
		this.lat = lat;
		this.lon = lon;
		this.address = address;
	}


	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	public String getAddress() {
		return address;
	}

}
