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
import oAuth.CreateAndVerify;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static int RANDOM = 0;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// check to see if client is already logged in by looking if a valid token is sent over in the header
		String token = BearerToken.getBearerToken(request);
		
		if(token != null && CreateAndVerify.isTokenValid(token, request.getRemoteAddr())) {
			JSONObject obj = new JSONObject();
			obj.put("status", "sorry but you have to log out to register for an account");
			RpcHelper.writeJsonObject(response, obj);
			//response.sendRedirect("index.html");
			
		} else {
			DBConnection connection = DBConnectionFactory.getConnection();
			
			Date date = new Date();
			SimpleDateFormat mf = new SimpleDateFormat("yyyyMMddhhmmss");
			String createTime = mf.format(date);
			String id = createTime + String.format("%05d", RANDOM++);
			if (RANDOM >= 100) {
				RANDOM = 0;
			}
			try {
				 JSONObject input = RpcHelper.readJSONObject(request);
				 String userId = input.getString("username") + id;
				 String username = input.getString("username");
				 String password = input.getString("password");
				 String email = input.getString("email");
				 String firstname = input.getString("first_name");
				 String lastname = input.getString("last_name");
			 
			 JSONObject obj = new JSONObject();
			 if(connection.registerUser(userId, username, password, email, firstname, lastname)) {
				 obj.put("status", "you have wooooooo offically joined the rainbowBunny club, welcome!");
			 }else {
				 obj.put("status", "Wooooo! userID already exists or something else is wrong I guess ^_^");
				 }
				RpcHelper.writeJsonObject(response, obj);
			} catch (Exception e) {
				 e.printStackTrace();
			} finally {
				 connection.close();
			}	
		}	
	}

}
