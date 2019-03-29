package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.DistanceMatrix;
import entity.DistanceMatrix.DistanceMatrixBuilder;
import entity.GeoLocation;

public class GoogleAPI {
	private static final String URL_DISTANCE_MATRIX = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private static final String DISTANCE_MATRIX_API_KEY = "";
	private static final String MODE = "bicycling";
	private static final String URL_GEOENCODING = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static final String GEOENCODING_API_KEY = "";
	
	public static GeoLocation getGeoEncoding(String address){
		
		String query = String.format("address=%s&key=%s", address, GEOENCODING_API_KEY);
		String url = URL_GEOENCODING + query;
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			//System.out.println("Send request to url: " + url);
			System.out.println("Response_getGeoEncoding code: " + responseCode);

			if (responseCode != 200) {
				return null;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject obj = new JSONObject(response.toString());
			if (!obj.isNull("results")) {
				return getGeoEncodingObj(obj.getJSONArray("results"), address);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static GeoLocation getGeoEncodingObj(JSONArray results, String address){
		try {
			JSONObject result = results.getJSONObject(0);
			JSONObject geometry = result.getJSONObject("geometry");
			JSONObject location  = geometry.getJSONObject("location");
			GeoLocation encoding = new GeoLocation(location.getDouble("lat"), location.getDouble("lng"), address);		
			
			return encoding;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<DistanceMatrix> getDistanceMatrix(GeoLocation origin, GeoLocation dest) {

		String query = String.format("units=imperial&origins=%s,%s&destinations=%s,%s&mode=%s&key=%s", origin.getLat(),
				                     origin.getLon(), dest.getLat(), dest.getLon(), MODE, DISTANCE_MATRIX_API_KEY);

		String url = URL_DISTANCE_MATRIX + query;

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			//System.out.println("Send request to url: " + url);
			System.out.println("Response_getDistanceMatrix code: " + responseCode);

			if (responseCode != 200) {
				return new ArrayList<DistanceMatrix>();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject obj = new JSONObject(response.toString());
			if (!obj.isNull("rows")) {
				return getDistanceMatrixList(obj.getJSONArray("rows"));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<DistanceMatrix>();
	}

	private static List<DistanceMatrix> getDistanceMatrixList(JSONArray rows) {
		ArrayList<DistanceMatrix> distanceMatrixList = new ArrayList<>();
		try {
			for (int i = 0; i < rows.length(); i++) {
				JSONObject row = rows.getJSONObject(i);
				JSONArray elements = row.getJSONArray("elements");
				for (int j = 0; j < elements.length(); j++) {
					JSONObject element = elements.getJSONObject(j);
					DistanceMatrixBuilder builder = new DistanceMatrixBuilder();
					if (!element.isNull("status") && element.getString("status").equals("OK")) {
						if (!element.isNull("duration")) {
							builder.setDuration_seconds(element.getJSONObject("duration").getInt("value"));
							builder.setDuration_text(element.getJSONObject("duration").getString("text"));
						}
						if (!element.isNull("distance")) {
							builder.setDistance_meters(element.getJSONObject("distance").getInt("value"));
							builder.setDistance_text(element.getJSONObject("distance").getString("text"));
						}
						distanceMatrixList.add(builder.build());
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distanceMatrixList;
	}

}
