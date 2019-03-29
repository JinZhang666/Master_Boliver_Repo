package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.BaseStatus;
import entity.BaseStatus.BaseStatusBuilder;
import entity.Order;
import entity.Order.OrderBuilder;
import recommendation.ClosestBaseToRobot;

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
			String sql = "INSERT INTO CurrentOrder VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
	   		ps.setString(7, order.getSender());
	   		ps.setString(8, order.getReceiver());
	   		ps.setString(9, order.geteArrival());
	   		ps.setString(10, order.getCreateTime());
	   		ps.setString(11, order.getCost());
	   		
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
	public boolean updateRobotStatus(String robotId, String destination, String newStatus, String baseId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try {
			String sql = "UPDATE robot SET robotStatus = ?, destination= ?, base_id = ? WHERE robot_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, newStatus);
			stmt.setString(2, destination);
			stmt.setString(3, baseId);
			stmt.setString(4, robotId);
			
			return stmt.executeUpdate() == 1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean updateOrderStatus(String orderId, String newStatus) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		String curTime = calendar.getTime().toString();
		try {
			String sql = "UPDATE orderHistory SET order_status = ?, a_arrival= ? WHERE order_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, newStatus);
			stmt.setString(2, curTime);
			
			return stmt.executeUpdate() == 1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean cancelOrder(String orderId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		return moveOrder(orderId, "4");
	}
	
	@Override
	public boolean confirmOrder(String orderId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		return moveOrder(orderId, "3");
	}
	
	@Override
	public boolean moveOrder(String orderId, String newStatus) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
			
		// get robot_id, curLocation of robot, given order_id
		String robotId = null;
		String curLocation = null;
		try {
			String sql = "SELECT currentorder.robot_id, robot.curLocation FROM currentorder INNER JOIN robot ON currentorder.robot_id = robot.robot_id WHERE order_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, orderId);
			
			
			ResultSet rsGetRobot = stmt.executeQuery();
			while(rsGetRobot.next()) {
				robotId = rsGetRobot.getString("robot_id");
				curLocation = rsGetRobot.getString("curLocation");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// calculate closest base and get its address
		String returnBaseAddr = ClosestBaseToRobot.getAddress(curLocation);
		
		// update robotStatus
		boolean updateRobot = updateRobotStatus(robotId, returnBaseAddr, "returning", "-1"); // {robotId, returning base address, new robot status, new base_id status(status remains -1 as it has not yet arrived at any base)}
		
		boolean deleteOrder = false;
		boolean moved = false;

		// copy paste order from currentOrder to orderHisotry
		try { // TODO
			String sql = "INSERT INTO orderhistory (order_id, robot_id, user_id, order_status, origin, destination, sender, receiver, e_arrival, create_time, cost)\r\n" + 
					     "SELECT order_id, robot_id, user_id, order_status, origin, destination, sender, receiver, e_arrival, create_time, cost\r\n" + 
					     "FROM currentorder \r\n" + 
					     "WHERE currentorder.order_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, orderId);
			
			moved = stmt.executeUpdate() == 1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// update orderStatus
		boolean updateOrder = updateOrderStatus(orderId, newStatus);
		
		// delete order from currentOrder
		try {
			String sql = "DELETE FROM currentorder WHERE order_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, orderId);
			
			deleteOrder = stmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// going to log each success or failure 
		if(updateRobot) {
			System.out.println("updateRobot is a success");

			if(moved) {
				System.out.println("moving order from current to history is a success");
				
				if(updateOrder) {
					System.out.println("updateOrder is a success");
					
					if(deleteOrder) {
						System.out.println("delete order from currentOrders is a success");
						return true;
						
					} else {
						System.out.println("delete order from currentOrders has failed");
					}
				} else {
					System.out.println("updating order has failed");
				}
			} else {
				System.out.println("moving order has failed");
			}
		} else {
			System.out.println("updateRobot has failed");
		}
		
		System.out.println("if you have reached here, this is the end of line before returning false of moveOrder( )");
		
		//return updateRobot && moved && updateOrder && deleteOrder;
		return false;
		
	}
}
