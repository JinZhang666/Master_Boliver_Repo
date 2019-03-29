package rpc;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.BearerToken;
import oAuth.CreateAndVerify;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String jwt = BearerToken.getBearerToken(request);
			String ipAddr = request.getRemoteAddr();
			JSONObject obj = new JSONObject();
			
			if(CreateAndVerify.isTokenValid(jwt, ipAddr)) {
				obj.put("wooo", "your token is still valid");
			}else {
				obj.put("invalid token","you cant use this one, try log in again for a new token");
			}
			
			RpcHelper.writeJsonObject(response, obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		DBConnection connection = DBConnectionFactory.getConnection();
		JSONObject obj = new JSONObject();
		
		try  {
			JSONObject input = RpcHelper.readJSONObject(request);
	   		 String username = input.getString("username");
	   		 String password = input.getString("password");
	   		 
	   		 if(connection.verifyLogin(username, password)) {
	   			Calendar cal = Calendar.getInstance();
	   			cal.add(Calendar.SECOND, 3600000);
	   			String token = CreateAndVerify.createToken(request.getRemoteAddr(), cal.getTimeInMillis());			
				//System.out.println("token::"+token);
				obj.put("access_token", token);					
	   		 }
	   		 else {
	   			response.setStatus(401);
	   			obj.put("status", "Apparently, your username either doesnt exist or your password is wrong");
	   		 }
	   		 
	   		RpcHelper.writeJsonObject(response, obj);
	   		
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

}
