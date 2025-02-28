package com.example.task_manager.unit.util;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtilTest {

    public static Key generateSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public static String generateTestToken(String email, String role, Key secretKey) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 час
                .signWith(secretKey)
                .compact();
    }
}
