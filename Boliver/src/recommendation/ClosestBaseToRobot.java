package recommendation;

import java.util.ArrayList;
import java.util.List;
import db.DBConnection;
import db.DBConnectionFactory;
import entity.BaseStatus;
import entity.GeoLocation;
import external.GoogleAPI;
import external.Haversince;

public class ClosestBaseToRobot {
	public static String getAddress(String curLocation) {
		DBConnection conn = DBConnectionFactory.getConnection();
		
		// pre-process curLocation
		char[] array = curLocation.toCharArray();
		for(int i = 0; i < array.length; i++) {
			if(array[i] == ' ') {
				array[i] = '+';
			}
		}
		String processedCurLocation = new String(array, 0, array.length);

		GeoLocation encodedCurLocation = GoogleAPI.getGeoEncoding(processedCurLocation);
		
		List<BaseStatus> baseList = new ArrayList<>();
		baseList = conn.getBaseStatus();
		List<GeoLocation> geoBaseList = new ArrayList<>();
		
		for(BaseStatus base : baseList) {
			GeoLocation tmp = new GeoLocation(Double.parseDouble(base.getLat()), Double.parseDouble(base.getLon()), base.getAddress());
			geoBaseList.add(tmp);
		}
		
		String baseAddress = null;
		double distance = 0.0;
		
		for(GeoLocation base : geoBaseList) {
			if(baseAddress == null) {			
				baseAddress = base.getAddress();			
				distance = Haversince.calculateDistance(base, encodedCurLocation);
				
			}else {			
				double res = Haversince.calculateDistance(base, encodedCurLocation);
				if(distance < res) {
					distance = res;
					baseAddress = base.getAddress();
				}
			}
		}
		
		return baseAddress;
	}
}
