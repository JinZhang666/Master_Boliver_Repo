package oAuth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.KeyConstants;


public class CreateAndVerify {

	private static String key = KeyConstants.SECRET_KEY_JWT;
	private static String base64Key = DatatypeConverter.printBase64Binary(key.getBytes());
	private static byte[] secretBytes = DatatypeConverter.parseBase64Binary(base64Key);	
	private static final Key signingKey = new SecretKeySpec(
			secretBytes, SignatureAlgorithm.HS256.getJcaName()
	);
	// GOOOD
	
	public static String createToken(String ipAddr,long expired) {
		//System.out.println(base64Key);
		//System.out.println(secretBytes.length);
		
		String token =  Jwts.builder()
					.setSubject(ipAddr)
					.signWith(CreateAndVerify.signingKey)
					.setIssuer("Boliver")
					.setExpiration(new Date(expired))					
					.compact();
		Boolean is_token_valid = false;
		
		try {
			is_token_valid =  Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject().equals(ipAddr);
			
		}
		catch(JwtException e) {
			//	Problem while parsing JWT
			System.out.println("Not able to parse JWT");
		}
		
		//System.out.println(is_token_valid);
		if(is_token_valid) {
			//	Store Token
			return token;
		}
		return "";
		
	}
	
	public static Boolean isTokenValid(String token, String ipAddr)throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
		/** 
		 *  Workflow of verifying token validitiy: check blackList -> check Expiration and IpAddr
		 */
		try {
			Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
			String ipDecode = claims.getSubject();
			DBConnection connection = DBConnectionFactory.getConnection();
			if(connection.isBlackListed(token)) {
				return false;
			} else if(ipDecode.equals(ipAddr)) {
				return true;
			}
		}		
		catch(ExpiredJwtException expiredException) {
			System.out.println("expired exception");
			//	Redirect to get new token and handle all scenarios
			return false;
			
		}		
		catch(Exception ex) {
			System.out.println(ex);
			System.out.println("----------------------------------------------------------------------------------");
			ex.printStackTrace();
			return false;
		}
		/**
		 * 	ClaimJwtException: thrown after a validation of a JTW claim failed
		 *	ExpiredJwtException: indicating that a JWT was accepted after it expired and must be rejected
		 *	MalformedJwtException: thrown when a JWT was not correctly constructed and should be rejected
		 *	PrematureJwtException: indicates that a JWT was accepted before it is allowed to be accessed and must be rejected
		 *	SignatureException: indicates that either calculating a signature or verifying an existing signature of a JWT failed
		 *	UnsupportedJwtException: thrown when receiving a JWT in a particular format/configuration that does not match the format expected by the application. 
		 *		For example, this exception would be thrown if parsing an unsigned plaintext JWT when the application requires a cryptographically signed Claims JWS instead
		 */
		
		return true;
	}
}
