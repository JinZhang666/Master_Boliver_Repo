package rpc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.BearerToken;
import entity.Order;
import entity.Order.OrderBuilder;
import external.CalETime;
import oAuth.CreateAndVerify;

/**
 * Servlet implementation class PlaceOrder
 */
@WebServlet("/submitorder")
public class SubmitOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static int RANDOM = 0;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitOrder() {
        super();
        
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Get token from request
		String token = BearerToken.getBearerToken(request);
		
		if(token != null && CreateAndVerify.isTokenValid(token, request.getRemoteAddr())) {
			// connect to database
			DBConnection conn = DBConnectionFactory.getConnection();
			// acquire parameters from front end
			JSONObject input = RpcHelper.readJSONObject(request);	
			
			try {
				//generate order ID and create time
				Date date = new Date();
				SimpleDateFormat mf = new SimpleDateFormat("yyyyMMddhhmmss");
				String createTime = mf.format(date);
				String orderId = createTime + String.format("%05d", RANDOM++);
				if (RANDOM >= 100) {
					RANDOM = 0;
				}
				
				OrderBuilder builder = new OrderBuilder();
				
				String robotId = conn.getRobotId(input.get("address").toString(), input.get("type").toString());
				String userId = input.getString("user_id");
				String origin = input.getString("origin");
				String destination = input.getString("destination");
				String eArrival = CalETime.calculateETime(input.getString("travel_time")); //calculate estimated arrival time
				String cost = input.getString("cost");
				String sender = input.getString("sender");
				String receiver = input.getString("receiver");
				
				builder.setOrderId(orderId);
				builder.setRobotId(robotId);
				builder.setUserId(userId);
				builder.setOrderStatus("2");
				builder.setOrigin(origin);
				builder.setDestination(destination);
				builder.seteArrival(eArrival);
				builder.setCreateTime(createTime);
				builder.setCost(cost);
				builder.setSender(sender);
				builder.setReceiver(receiver);
				
				Order order = builder.build();
				JSONObject obj = new JSONObject();
				
				if(conn.placeOrder(order)) {
					obj.put("status", "your order has been created");
				} else {
					response.setStatus(401);
					obj.put("status", "something went wrong when trying to create your order");
				}
				if(conn.updateRobotStatus(order.getRobotId(), order.getDestination(), "retrieving", "-1")) {
					obj.put("robot_status", "assigned robot has received your order and is now on its way to retrieve the package");
				} else {
					
				}
				RpcHelper.writeJsonObject(response, obj);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.close();
			}
			
		} else { // if your token is invalid 
			JSONObject obj = new JSONObject();
			obj.put("status", "are you trying to gain illegal access? Where is your token?");
			RpcHelper.writeJsonObject(response, obj);
		}

	}
}
