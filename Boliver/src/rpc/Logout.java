package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.BearerToken;


/**
 * Servlet implementation class Logout
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/**
		 * get token, if it is sent over, 
		 * add the token to the blacklist to simulate an user-initiated-action of invalidating the token 
		 */
		
		try {
			JSONObject obj = new JSONObject();
			String token = BearerToken.getBearerToken(request);
	        if (token != null) {
	            DBConnection connection = DBConnectionFactory.getConnection();
	            if(connection.addToBlackList(token)) {
	            	obj.put("status", "you have successfully logged out and your token has been trashed");
	            } else {
	            	obj.put("status", "I tried to invalidate your token, but I failed");
	            }	            
	        } else {
	        	obj.put("status", "I did not receive your token, did you clear your cache?");
	        }
			
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//response.sendRedirect("index.html");
	}

}
