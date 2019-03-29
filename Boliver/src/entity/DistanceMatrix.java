package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class DistanceMatrix {
	private int duration_seconds;
	private String duration_text;
	private int distance_meters;
	private String distance_text;

	private DistanceMatrix(DistanceMatrixBuilder builder) {
		this.duration_seconds = builder.duration_seconds;
		this.duration_text = builder.duration_text;
		this.distance_meters = builder.distance_meters;
		this.distance_text = builder.distance_text;
	}

	public int getDuration_seconds() {
		return duration_seconds;
	}

	public String getDuration_text() {
		return duration_text;
	}

	public int getDistance_meters() {
		return distance_meters;
	}

	public String getDistance_text() {
		return distance_text;
	}

	public JSONObject toJsonObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("duration_seconds", this.duration_seconds);
			obj.put("duration_text", this.duration_text);
			obj.put("distance_meters", this.distance_meters);
			obj.put("distance_text", this.distance_text);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static class DistanceMatrixBuilder {
		private int duration_seconds;
		private String duration_text;
		private int distance_meters;
		private String distance_text;

		public DistanceMatrix build() {
			return new DistanceMatrix(this);
		}

		public void setDuration_seconds(int duration_seconds) {
			this.duration_seconds = duration_seconds;
		}

		public void setDuration_text(String duration_text) {
			this.duration_text = duration_text;
		}

		public void setDistance_meters(int distance_meters) {
			this.distance_meters = distance_meters;
		}

		public void setDistance_text(String distance_text) {
			this.distance_text = distance_text;
		}
	}
}
