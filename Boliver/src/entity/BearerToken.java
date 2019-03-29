package entity;

import javax.servlet.http.HttpServletRequest;

public class BearerToken {
	
	private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String AUTH_HEADER_VALUE_PREFIX = "Bearer";
    
	public static String getBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER_KEY);
        
        System.out.println("HTTPRequestHeader: " + authHeader); // What does Header look like?
        
        if ( authHeader != null && authHeader.startsWith(AUTH_HEADER_VALUE_PREFIX) ) {
            return authHeader.substring(AUTH_HEADER_VALUE_PREFIX.length());
        }
        return null;
    }
}
