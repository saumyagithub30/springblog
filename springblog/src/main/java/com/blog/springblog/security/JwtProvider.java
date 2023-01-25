package com.blog.springblog.security;

import com.blog.springblog.exception.SpringBlogException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore =  KeyStore.getInstance("JKS");
            InputStream inputStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(inputStream, "secret".toCharArray());
        } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringBlogException("Exception occured while loading keystore");
        }

    }
    public String generateToken(Authentication authentication)  {

        long expirationTime = 864_000_000; // 10 days in milliseconds

        // Create the JWT
        JwtBuilder builder = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .signWith(getPrivateKey());

        // Add the expiration time
        builder.setExpiration(new Date(System.currentTimeMillis() + expirationTime));

        // Build the JWT and serialize it to a compact, URL-safe string
        return builder.compact();

    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringBlogException("Exception occured while retrieving Private Key from keystore");
        }

    }

    public boolean validateToken1(String jwt) {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJwt(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringBlogException("Exception occured while retrieving Private Key from keystore");
        }

    }

    public boolean validateToken(String token) {

        try {
            // Parse the JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(getPublicKey())
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
                    .setSigningKey(getPublicKey())
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
