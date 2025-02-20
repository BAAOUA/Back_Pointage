package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.example.Pointage.Configuration.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(SpringExtension.class)

public class JwtServiceTest {
  @Mock
  private JwtService jwtService;
  

  @Test
  void testGenerateAccesAndRefreshToken(){

    given(jwtService.generateAccessToken(anyString())).willReturn("accessToken");
    given(jwtService.generateRefreshToken(anyString())).willReturn("refreshToken");
    
    String accesstoken = jwtService.generateAccessToken("username");
    String refreshtoken = jwtService.generateRefreshToken("username");

    assertEquals(accesstoken, "accessToken");
    assertEquals(refreshtoken, "refreshToken");
  }
  @Test
  void testExtractUsername(){

    given(jwtService.generateAccessToken(anyString())).willReturn("accessToken");
    
    String token = jwtService.generateAccessToken("username");

    assertEquals(token, "accessToken", "hdjbnkjref");
  }
  @Test
  public void testExtractAllClaims() {
  // Arrange
        String secretKey = "mySecretKey";
        String userName = "testUser";
        
        // Créer un token JWT valide pour tester
        String token = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // Expiration dans 1 heure
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Appel à la méthode extractAllClaims avec un token valide
        Claims claims = jwtService.extractAllClaims(token);

        // Assert
        assertNotNull(claims, "Claims should not be null");
        assertEquals(userName, claims.getSubject(), "Username should match the token's subject");
        assertTrue(claims.getExpiration().after(new Date()), "Expiration date should be in the future");
    }
}
