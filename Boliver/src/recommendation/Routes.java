package recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.BaseStatus;
import entity.DistanceMatrix;
import entity.GeoLocation;
import entity.robotInfo;
import external.CalDrone;
import external.GoogleAPI;
import external.Haversince;

public class Routes {
	public static JSONObject calculateRoutes(String origin, String destination) {
		
		// Convert address into lat,lon
		GeoLocation encoding_origin = GoogleAPI.getGeoEncoding(origin);
		GeoLocation encoding_dest = GoogleAPI.getGeoEncoding(destination);
		
		// Get status on available robots in each base
		DBConnection conn = DBConnectionFactory.getConnection(); // Connect to database
		List<BaseStatus> baseStatus = conn.getBaseStatus();		 // get status for each base on its drone and ground robot availability, and each of their addresses
		
		List<GeoLocation> robotBasesAvail = new ArrayList<>();
		List<GeoLocation> droneBasesAvail = new ArrayList<>();
		 
		for(BaseStatus base : baseStatus) { // iterate over all bases and add them to the two lists declared above given their status on bot availability
			if(base.getGroundstatus()) {
				robotBasesAvail.add(new GeoLocation(Double.parseDouble(base.getLat()), Double.parseDouble(base.getLon()), base.getAddress()));
			}
			if(base.getDroneStatus()) {
				droneBasesAvail.add(new GeoLocation(Double.parseDouble(base.getLat()), Double.parseDouble(base.getLon()), base.getAddress()));
			}
		}
		 
		// Get the closest ground robot base and Distance Matrix based on travel time.
		GeoLocation ClosestGrobotBase = null; // Select base based on the distance and availability.
		DistanceMatrix dMatrixOfCGB = null;
		for (GeoLocation base : robotBasesAvail) {
			DistanceMatrix dm = GoogleAPI.getDistanceMatrix(base, encoding_origin).get(0);
		
			if (ClosestGrobotBase == null) {
				ClosestGrobotBase = base;
				dMatrixOfCGB = dm;
			} else {
				if (dm.getDuration_seconds() < dMatrixOfCGB.getDuration_seconds()) {
					dMatrixOfCGB = dm;
					ClosestGrobotBase = base;
				}
			}
		}
		
		// Get the closest drone base and Distance Matrix based on travel time.
		GeoLocation ClosestDroneBase = null; // Select base based on the distance and availability.
		double distanceOfCDB = 0.0;
		for (GeoLocation base : droneBasesAvail) {
			double distance = Haversince.calculateDistance(base, encoding_origin);
		
			if (ClosestDroneBase == null) {
				ClosestDroneBase = base;
				distanceOfCDB = distance;
			} else {
				if (distance < distanceOfCDB){
					distanceOfCDB = distance;
					ClosestDroneBase = base;
				}
			}
		}
		
		// Get travel distance and time for groundRobots from pickup location to destination
		DistanceMatrix groundRobot = GoogleAPI.getDistanceMatrix(encoding_origin, encoding_dest).get(0);
		// Get travel distance and time for drones pickup location to destination
		Map<String, Double> drone = CalDrone.calculateDrone(encoding_origin, encoding_dest);
		//
		double dronePrice = robotInfo.getDroneRate() * (drone.get("distance") + distanceOfCDB);
		//
		double groundPrice = robotInfo.getGroundRobotRate() * ((groundRobot.getDistance_meters() + dMatrixOfCGB.getDistance_meters())/1609.344);
		
		System.out.println("returning object"); // <--- to show in console that I have successfully reached this point
			
		JSONObject mainObj = new JSONObject();
		JSONObject addr = new JSONObject();
		JSONObject droneObj = new JSONObject();
		JSONObject groundBotObj = new JSONObject();
		
		addr.put("origin", origin)
			.put("destiantion", destination);
		
		droneObj.put("travel_time", drone.get("time")) // time unit = minutes
		        .put("cost", dronePrice)
		        .put("pickup_time", ClosestDroneBase == null ? "no available drone, please wait" : distanceOfCDB/drone.get("speed"))
		        .put("travel_distance", drone.get("distance") + distanceOfCDB) // distance unit = miles
		        .put("avail_status", ClosestDroneBase == null ? "no" : "yes")
		        .put("base", ClosestDroneBase == null ? "no available drone, please wait" : ClosestDroneBase.getAddress());
		
		groundBotObj.put("travel_time", groundRobot.getDuration_seconds()/60) // time unit = minutes
			        .put("cost", groundPrice)
			        .put("pickup_time", ClosestGrobotBase == null ? "no available groundBot, please wait" : dMatrixOfCGB.getDuration_seconds()/60)
			        .put("travel_distance", (groundRobot.getDistance_meters() + dMatrixOfCGB.getDistance_meters())/1609.344) // distance unit = miles
			        .put("avail_status", ClosestGrobotBase == null ? "no" : "yes")
			        .put("base", ClosestGrobotBase == null ? "no available groundBot, please wait" : ClosestGrobotBase.getAddress());
		
		mainObj.put("DeliveryAddress", addr)
			   .put("Drone", droneObj)
			   .put("GroundBot", groundBotObj);
		     
		return mainObj;
	}

}
