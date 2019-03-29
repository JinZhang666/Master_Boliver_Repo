package external;

import java.util.HashMap;
import java.util.Map;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.GeoLocation;

public class CalDrone {
	public static Map<String, Double> calculateDrone(GeoLocation origin, GeoLocation dest) {
		Map<String, Double> map = new HashMap<>();
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			int speedPerHour = Integer.parseInt(conn.getDroneSpeed("drone"));
			//System.out.println("speedPerHour is: " + speedPerHour);
			
			double speedPerMin = speedPerHour / 60.0;
			//System.out.println("speedPerMinute is: " + speedPerMin);
			
			double distance = Haversince.calculateDistance(origin,dest);
			//System.out.println("HaverDistance is: " + distance);
			
			double time = distance/speedPerMin;
			
			map.put("distance", distance);
			map.put("time", time);
			map.put("speed", speedPerMin);
			return map;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
