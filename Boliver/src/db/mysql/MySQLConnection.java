package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.BaseStatus;
import entity.BaseStatus.BaseStatusBuilder;
import entity.Order;
import entity.Order.OrderBuilder;

public class MySQLConnection implements DBConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean verifyLogin(String username, String password) {
		if (conn == null) {
			return false;
		}
		try {
			String sql = "SELECT user_id FROM users WHERE username = ? AND pwd = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean isBlackListed(String token) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try { 
			
			String sql = "SELECT token FROM blacklist WHERE token = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, token);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean addToBlackList(String token) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String sql = "INSERT IGNORE INTO BlackList VALUES(?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, token);
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean registerUser(String userId, String username, String password, String email, String firstname,
			String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String sql = "INSERT IGNORE INTO users VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, username);
			ps.setString(3, password);
			ps.setString(4, email);
			ps.setString(5, firstname);
			ps.setString(6, lastname);

			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getDroneSpeed(String type) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		try {
			String sql = "SELECT speed FROM robotType WHERE type = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, type);

			ResultSet rs = ps.executeQuery();
			String[] speed = new String[1];
			while(rs.next()) {
				speed[0] = rs.getString("speed");
				// for debug: System.out.println("speeeeeeeeed: " + speed);
			}
			return speed[0];
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Set<Order> getHistoryOrders(String userId, Integer start, Integer end) {
		
		if (conn == null) {
			System.out.println("DB connection failed for getCurrentOrders getHistoryOrders");
			return new HashSet<>();
		}
		Set<Order> historyOrders = new HashSet<>();
		try {
			String sql = "SELECT a.user_id user_id,a.order_id order_id,a.robot_id robot_id,a.order_status order_status,"
					     + "a.origin origin,a.destination destination,a.e_arrival e_arrival,a.a_arrival a_arrival,"
					     + "a.create_time create_time,a.cost cost,c.type type From orderHistory a,Robot b,Robottype c"
					     + " where a.user_id = ? and a.robot_id=b.robot_id and b.type_id=c.type_id";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);

			ResultSet rs = stmt.executeQuery();

			OrderBuilder builder = new OrderBuilder();

			while (rs.next()) {		
				builder.setUserId(rs.getString("user_id"));
				builder.setOrderId(rs.getString("order_id"));
				builder.setRobotId(rs.getString("robot_id"));
				builder.setOrderStatus(rs.getString("order_status"));
				builder.setOrigin(rs.getString("origin"));
				builder.setDestination(rs.getString("destination"));
				builder.seteArrival(rs.getString("e_arrival"));
				builder.setaArrival(rs.getString("a_arrival"));
				builder.setCreateTime(rs.getString("create_time"));
				builder.setCost(rs.getString("cost"));
				builder.setRobotType(rs.getString("type"));
				historyOrders.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return historyOrders;
	}
	
	@Override
	public Set<Order> getCurrentOrders(String userId){
		if (conn == null) {
			System.out.println("DB connection failed for getCurrentOrders");
			return new HashSet<>();
		}
		Set<Order> currentOrders = new HashSet<>();
		try {
			String sql = "SELECT currentorder.order_id, currentorder.robot_id, robotType.type, currentorder.order_status, robot.curLocation, currentorder.origin, currentorder.destination, currentorder.e_arrival, currentorder.create_time, currentorder.cost   \r\n" + 
					"\r\n" + 
					"FROM currentOrder\r\n" + 
					"INNER JOIN robot ON currentOrder.robot_id = robot.robot_id \r\n" + 
					"INNER JOIN robotType ON robot.type_id = robotType.type_id\r\n" + 
					"WHERE currentorder.user_id = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);

			ResultSet rs = stmt.executeQuery();

			OrderBuilder builder = new OrderBuilder();

			while (rs.next()) {		
				builder.setOrderId(rs.getString("order_id"));
				builder.setRobotId(rs.getString("robot_id"));
				builder.setOrderStatus(rs.getString("order_status"));
				builder.setOrigin(rs.getString("origin"));
				builder.setDestination(rs.getString("destination"));
				builder.seteArrival(rs.getString("e_arrival"));
				builder.setCreateTime(rs.getString("create_time"));
				builder.setCost(rs.getString("cost"));
				builder.setRobotType(rs.getString("type"));
				currentOrders.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return currentOrders;
	}

	@Override
	public boolean placeOrder(Order order) {
		if (conn == null) {
			System.out.println("DB connection failed");
			return false;
		}
		try {
			String sql = "INSERT INTO CurrentOrder VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	   		PreparedStatement ps = conn.prepareStatement(sql);
	   		
	   		if (
	   				order.getOrderId() == null && 
	   				order.getRobotId() == null && 
	   				order.getUserId() == null && 
	   				order.getOrderStatus() == null && 
	   				order.getOrigin() == null && 
	   				order.getDestination() == null &&
					order.geteArrival() == null &&
					order.getCreateTime() == null &&
					order.getCost() == null &&
					order.getSender() == null &&
					order.getReceiver() == null
   				) {
	   			System.out.println("There is null in the data.");
	   			return false;
	   		}
	   		
	   		ps.setString(1, order.getOrderId());
	   		ps.setString(2, order.getRobotId());
	   		ps.setString(3, order.getUserId());
	   		ps.setString(4, order.getOrderStatus());
	   		ps.setString(5, order.getOrigin());
	   		ps.setString(6, order.getDestination());
	   		ps.setString(6, order.getSender());
	   		ps.setString(7, order.getReceiver());
	   		ps.setString(8, order.geteArrival());
	   		ps.setString(9, order.getCreateTime());
	   		ps.setString(10, order.getCost());
	   		
	   		//System.out.println(ps);
	   		
	   		return ps.executeUpdate() == 1;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public Set<Order> trackOrder(String orderId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		Set<Order> results = new HashSet<>();
		try {
			String sql ="SELECT currentorder.order_id, currentorder.robot_id, robotType.type, robot.curLocation, currentorder.origin, currentorder.destination, currentorder.e_arrival, currentorder.create_time, currentorder.cost   \r\n" + 
					"\r\n" + 
					"FROM currentOrder\r\n" + 
					"INNER JOIN robot ON currentOrder.robot_id = robot.robot_id \r\n" + 
					"INNER JOIN robotType ON robot.type_id = robotType.type_id\r\n" + 
					"WHERE order_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, orderId);

			ResultSet rs = stmt.executeQuery();

			OrderBuilder builder = new OrderBuilder();

			while (rs.next()) {		
				builder.setOrderId(rs.getString("currentorder.order_id"));
				builder.setRobotId(rs.getString("currentorder.robot_id"));
				builder.setRobotType(rs.getString("robotType.type"));
				builder.setCurrentLocation(rs.getString("robot.curLocation"));
				builder.setOrigin(rs.getString("currentorder.origin"));
				builder.setDestination(rs.getString("currentorder.destination"));
				builder.seteArrival(rs.getString("currentorder.e_arrival"));
				builder.setCreateTime(rs.getString("currentorder.create_time"));
				builder.setCost(rs.getString("currentorder.cost"));
				results.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return results;
	}
	
	@Override
	public List<BaseStatus> getBaseStatus(){
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		
		List<BaseStatus> results = new ArrayList<>();
		
		try {
			for(int i = 1; i < 4; i++) {
				String sql = "SELECT type_id, lat, lon, address FROM `robot` INNER JOIN base ON robot.base_id = base.base_id WHERE robot.base_id = ? GROUP BY type_id";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, i);
				ResultSet rs = stmt.executeQuery();
				BaseStatusBuilder builder = new BaseStatusBuilder();
				builder.setBaseId(i);
				boolean flag = false;
				while(rs.next()) {
					if(rs.getString("type_id").equals("1")) {
						builder.setGround(true);
					}
					if(rs.getString("type_id").equals("2")) {
						builder.setDrone(true);
					}
					if(!flag) {
						builder.setLat(rs.getString("lat"));
						builder.setLon(rs.getString("lon"));
						builder.setAddress(rs.getString("address"));
						flag = true;
					}
				}
				results.add(builder.build());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}
	
	@Override
	public String getRobotId(String baseAddress, String robotType) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		
		try {
			String sql = "SELECT robot_id FROM `robot` "
					+ "INNER JOIN base ON robot.base_id = base.base_id "
					+ "INNER JOIN robotType ON robot.type_id = robottype.type_id "
					+ "WHERE address = ? AND robotType.type = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, baseAddress);
			stmt.setString(2, robotType);
			
			ResultSet rs = stmt.executeQuery();
			String robotId = null;
			while(rs.next()) {
				robotId = rs.getString("robot_id");
				return robotId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean updateRobotStatus(String robotId, String destination) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String sql = "";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			return stmt.executeUpdate() == 1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
