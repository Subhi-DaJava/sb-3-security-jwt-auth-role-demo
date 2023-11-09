package com.uyghrujava.security.jwt_service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtTokenUtil {
    public static final long JWT_TOKEN_VALIDITY = 60 * 60;

    @Value("${jwt.secret.sign.key}")
    private String SECRET_KEY;

    // retrieve username from valid JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    // retrieve expiration date from valid JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // check if the token has expired or not
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    // retrieve claim from valid JWT token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);

        if(claims != null) {
            return claimsResolver.apply(claims);
        }

        return null;
    }

    // for retrieving any information from a valid Token, the secret key is required
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.error("Error occurred while parsing claims from token: {}", ex.getMessage());
            ex.getCause();
        }
        return null;
    }

    // generate token for USER
    public String generateToken(UserDetails userValueObject) {
        Map<String, Object> claims = new HashMap<>();
        // add a role to claims
        claims.put("role", userValueObject.getAuthorities());
        return generateToken(claims, userValueObject.getUsername());
    }

    // generate Refresh token for USER
    public String generateRefreshToken(UserDetails userValueObject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userValueObject.getAuthorities());
        return generateRefreshToken(claims, userValueObject.getUsername());
    }

    // generate new Token for USER
    private String generateToken(Map<String, Object> claims, String username) {
        Header header = Jwts.header();
        header.setType("JWT");

        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // generate new Refresh Token for USER
    private String generateRefreshToken(Map<String, Object> claims, String username) {
        Header header = Jwts.header();
        header.setType("JWT");

        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (JWT_TOKEN_VALIDITY * 1000 * 2)))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // validate Token
    public Boolean validateToken(String token, UserDetails userValueObject) {
        final String username = getUsernameFromToken(token);

         if(username == null) {
            return false;
        }
        return (username.equals(userValueObject.getUsername()) && !isTokenExpired(token));
    }

}
