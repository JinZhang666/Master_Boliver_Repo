package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import entity.BearerToken;
import oAuth.CreateAndVerify;
import recommendation.Routes;

/**
 * Servlet implementation class SearchRoute
 */
@WebServlet("/searchroute")
public class SearchRoute extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchRoute() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get token from request
		String token = BearerToken.getBearerToken(request);
		if(token != null && CreateAndVerify.isTokenValid(token, request.getRemoteAddr())) {
			
			JSONObject input = RpcHelper.readJSONObject(request);
			try {
				// Get origin and destination from request
				String origin = input.getString("origin");
				String dest = input.getString("destination");
				
				JSONObject result = Routes.calculateRoutes(origin, dest);
				
				RpcHelper.writeJsonObject(response, result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {  // if your token is invalid,
			JSONObject obj = new JSONObject();
			obj.put("status", "are you trying to gain illegal access? Where is your token?");
			RpcHelper.writeJsonObject(response, obj);
		}
	
	}

}
