package com.example.task_manager.util;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final long EXPIRATION_TIME = 86400000;
    private final SecretKey secretKey;

    public JwtUtil() {

        this.secretKey = Keys.hmacShaKeyFor(SecretKeyReader.getSecretKey().getBytes());
    }

    public String generateToken(String email, String role, UUID id) {

        return Jwts.builder()
            .setSubject(email)
            .claim("id", id)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractId(String token) {
       
        Claims claims = extractAllClaims(token);

        return UUID.fromString(claims.get("id", String.class));
    }

    public boolean validateToken(String token, String email) {
        
        String emailFromToken = extractEmail(token);
        return emailFromToken.equals(email) && !isTokenExpired(token);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractRoleFromToken(String token) {
        
        Claims claims = extractAllClaims(token);

        return claims.get("role", String.class);
    }
}
