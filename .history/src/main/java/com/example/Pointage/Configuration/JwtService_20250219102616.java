package com.example.Pointage.Configuration;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtService {

  @Value("${jwt-key}")
  private String jwtSecret;

  @Value("${refresh-expiration}")
  private long refresh;
  @Value("${access-expiration}")
  private long access;

  // Generate token with given user name
  public String generateAccessToken(String userName) {
      Map<String, Object> claims = new HashMap<>();
      return createToken(claims, userName, access);
  }

  public String generateRefreshToken(String userName) {
      Map<String, Object> claims = new HashMap<>();
      return createToken(claims, userName, refresh);
  }
  private String createToken(Map<String, Object> claims, String userName, long duree) {
      return Jwts.builder()
              .setClaims(claims)
              .setSubject(userName)
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis() + duree))
              .signWith(getSignKey(), SignatureAlgorithm.HS256)
              .compact();
  }

  private Key getSignKey() {
      byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
      return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
  }

  public Claims extractAllClaims(String token) {
      return Jwts.parserBuilder()
              .setSigningKey(getSignKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
  }

  public boolean isTokenExpired(String token) {
      try {
          Date expirationDate = extractExpiration(token); 
          System.out.println(expirationDate + " aftrer new date  "+ new Date());
          return expirationDate.before(new Date());
      } catch (Exception e) {
          e.printStackTrace();
          return false;
      }
  }
  public Boolean isTokenValide(String token) {
      try {
          Jwts.parserBuilder().setSigningKey(getSignKey()).build().parse(token);
          return true;
      } catch (Exception e) {
        return false;
      }
  }
}
