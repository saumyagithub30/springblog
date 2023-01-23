package com.blog.springblog.security;

import com.blog.springblog.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Service
public class JwtProvider {

    private Key key;

    @PostConstruct
    public void init() {
        key =  Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    public String generateToken(Authentication authentication)  {
//        String username = authentication.getName();
//        return Jwts.builder()
//                .setSubject(username)
//                .signWith(key)
//                .compact();

        long expirationTime = 864_000_000; // 10 days in milliseconds

        // Create the JWT
        JwtBuilder builder = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .signWith(key);

        // Add the expiration time
        builder.setExpiration(new Date(System.currentTimeMillis() + expirationTime));

        // Build the JWT and serialize it to a compact, URL-safe string
        return builder.compact();

    }

    public boolean validateToken1(String jwt) {
        Jwts.parser().setSigningKey(key).parseClaimsJwt(jwt);
        return true;
    }

    public boolean validateToken(String token) {

        try {
            // Parse the JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            // Check the expiration time
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException e) {
            // If the token is invalid, it will throw a JwtException
            return false;
        }
    }
    public String getUsernameFromJWT(String token) {

        try {
            // Parse the JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            // If the token is invalid, it will throw a JwtException
            System.out.println(e.getStackTrace());
            return null;
        }

    }
}
