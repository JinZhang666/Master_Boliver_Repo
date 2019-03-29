package external;

import entity.GeoLocation;

public class Haversince {
	public static final double R = 3959.87433; // in miles
	public static double calculateDistance(GeoLocation origin, GeoLocation dest) {
		double lat1 = origin.getLat();
		double lon1 = origin.getLon();
		double lat2 = dest.getLat();
		double lon2 = dest.getLon();
		
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (R * c);
	}
}
