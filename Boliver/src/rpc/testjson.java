package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class testjson
 */
@WebServlet("/testjson")
public class testjson extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public testjson() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject obj = new JSONObject();
		JSONObject addr = new JSONObject();
		JSONArray drone = new JSONArray();
		JSONArray groundBot = new JSONArray();
		
		JSONObject one = new JSONObject();
		JSONObject two = new JSONObject();
		JSONObject three = new JSONObject();
		JSONObject a = new JSONObject();
		JSONObject b = new JSONObject();
		JSONObject c = new JSONObject();
		
		one.put("travel_time", "0.4461667838100305");
		two.put("cost", "5.915172631323721");
		three.put("pickup_time", "4.2859713212489465");
		a.put("travel_time", "6");
		b.put("cost", "0.9929511651952596");
		c.put("pickup_time", "2");
		
		addr.put("origin", "3369+Mission+St,+San+Francisco,+CA+94110")
			.put("destination", "448+Cortland+Ave,+San+Francisco,+CA+94110");
		
		drone.put(0,one)
		     .put(1,two)
		     .put(2,three);
		
		groundBot.put(0, a)
		         .put(1, b)
		         .put(2, c);
		
		obj.put("Address", addr)
		   .put("Drone", drone)
		   .put("groundBot", groundBot);
		RpcHelper.writeJsonObject(response, obj);
	}


}
