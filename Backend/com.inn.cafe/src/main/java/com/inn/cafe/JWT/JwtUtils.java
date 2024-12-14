package com.inn.cafe.JWT;


import com.inn.cafe.serviceimple.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

//    ----secret key to on which our json token will be generated
private final Key secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

//    function extract username from the token
    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }

//    function to extract expirations from the token
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }


//----Function to extract claims (data) from a JWT (JSON Web Token)
    public <T> T extractClaims(String tokens, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(tokens);
        return claimsResolver.apply(claims);
    }

//    ----function to extract claims from a JWT token:
    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

//------function to validate the token either token is expired or not
private boolean isTokenExpired(String token) {
    try {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    } catch (Exception ex) {
        log.error("Error checking token expiration: {}", ex.getMessage());
        return true;
    }
}

    //    function fot generate the token
    public String generateToken(String username , String role){
        log.info("Generating token for username: {} with role: {}", username, role);
        Map<String,Object> claims=new HashMap<>();
        claims.put("role",role);
        return createToken(claims,username);
    }

//    function to create the token
    private String createToken(Map<String,Object> claims, String subject){
        return  Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256,secret).compact();
    }

//    function to validate the token
public boolean validateToken(String token, UserDetails userDetails) {
    try {
        final String username = extractUsername(token);
        if (username == null) {
            log.warn("Extracted username is null.");
            return false;
        }
        if (!username.equals(userDetails.getUsername())) {
            log.warn("Token username does not match userDetails.");
            return false;
        }
        if (isTokenExpired(token)) {
            log.warn("Token expired.");
            return false;
        }
        return true;
    } catch (Exception ex) {
        log.error("Exception while validating token: {}", ex.getMessage());
        return false;
    }
}

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

}
