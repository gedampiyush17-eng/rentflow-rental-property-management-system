package com.rentflow.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET=
            "rentflowrentflowrentflowrentflowrentflow12345678";

    private final Key key= Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email, String role){

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){

        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email){

        return extractUsername(token).equals(email)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey)key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
