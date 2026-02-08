package com.inn.cafe.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.String;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;          // مهم جداً
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {

    private String secret=" Z3VVeFdqYlNqZWxqa2hhamtsb2phZGtqYWhqc2Rhd2phc2RsanNkams";
    public String extractUsername(String token) {
        return extractClaims(token,Claims::getSubject);
    }
    public Date extractExpiration(String token) {
       return extractClaims(token,Claims::getExpiration);
    }
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    private  Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username,String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims,username);
    }
    private String createToken(Map<String, Object> claims , String subject) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expirationDate = new Date(now + 1000L * 60 * 60 * 10); // 10 ساعات

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)  // تمرير الـDate مباشرة
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = this.extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
