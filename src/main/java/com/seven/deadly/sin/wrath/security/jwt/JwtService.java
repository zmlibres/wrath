package com.seven.deadly.sin.wrath.security.jwt;

import com.seven.deadly.sin.wrath.security.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
    public static final long ACCESS_EXPIRATION = 1000 * 60 * 15; // 15 mins
    public static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(CustomUserDetails user) {
        return Jwts.builder()
                   .setSubject(user.getUsername())
                   .claim("userId", user.getUserId())
                   .claim("type", ACCESS)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                   .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                   .compact();

    }

    public String generateRefreshToken(CustomUserDetails user) {
        return Jwts.builder()
                   .setSubject(user.getUsername())
                   .claim("type", REFRESH)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                   .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

    public boolean isTokenValid(String token, CustomUserDetails userDetails) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) &&
                isAccessToken(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isAccessToken(String token) {
        return ACCESS.equals(extractClaim(token, c -> c.get("type", String.class)));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSecretKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaim(token, c -> c.get("userId", String.class));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH.equals(extractClaim(token, c -> c.get("type", String.class)));
    }


}
