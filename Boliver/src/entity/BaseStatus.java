package entity;

public class BaseStatus {
	private int baseId;
	private boolean drone;
	private boolean ground;
	private String lat;
	private String lon;
	private String address;
	
	private BaseStatus(BaseStatusBuilder builder) {
		this.baseId = builder.baseId;
		this.drone = builder.drone;
		this.ground = builder.ground;
		this.lat = builder.lat;
		this.lon = builder.lon;
		this.address = builder.address;
	}
	
	public static class BaseStatusBuilder{
		private int baseId;
		private boolean drone;
		private boolean ground;
		private String lat;
		private String lon;
		private String address;
		
		public void setBaseId(int baseId) {
			this.baseId = baseId;
		}
		
		public void setDrone(boolean drone) {
			this.drone = drone;
		}
		
		public void setGround(boolean ground) {
			this.ground = ground;
		}
		
		public void setLat(String lat) {
			this.lat = lat;
		}
		
		public void setLon(String lon) {
			this.lon = lon;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public BaseStatus build() {
			return new BaseStatus(this);
		}
		
	}
	
	public int getBaseId() {
		return baseId;
	}
	public boolean getDroneStatus() {
		return drone;
	}
	public boolean getGroundstatus() {
		return ground;
	}
	public String getLat() {
		return lat;
	}
	public String getLon() {
		return lon;
	}
	public String getAddress() {
		return address;
	}

}
