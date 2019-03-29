package db;

import java.util.List;
import java.util.Set;

import entity.BaseStatus;
import entity.Order;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();

	public boolean verifyLogin(String userId, String password);
	
	public boolean isBlackListed(String token);
	
	public boolean addToBlackList(String token);
	
	public boolean registerUser(String userId, String username, String password, String email, String firstname,
			                    String lastname);

	public Set<Order> getHistoryOrders(String userId, Integer start, Integer end);
	
	public boolean placeOrder(Order order);
	
	public Set<Order> trackOrder(String orderId);
	
	public String getDroneSpeed(String type);
	
	public Set<Order> getCurrentOrders(String userId);
	
	public List<BaseStatus> getBaseStatus();
	
	public String getRobotId(String baseAddress, String robotType);
	
	public boolean updateRobotStatus(String robotId, String destination, String newStatus, String baseId);
	
	public boolean moveOrder(String orderI, String newStatus);
	
	public boolean updateOrderStatus(String orderId, String newStatus);
	
	public boolean cancelOrder(String oderId);
	
	public boolean confirmOrder(String oderId);
}
