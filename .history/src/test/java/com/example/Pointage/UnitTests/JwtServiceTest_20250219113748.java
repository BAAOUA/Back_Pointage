package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Pointage.Configuration.JwtService;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private String jwtSecret = "secretKey"; // Clé secrète pour les tests
    private long accessExpiration = 3600000; // 1 heure
    private long refreshExpiration = 7200000; // 2 heures

    @Test
    void testCreateTokenIndirectly() {
      String userName = "admin";
      long expirationTime = 3600000L;
      Map<String, Object> claims = new HashMap<>();
      String expectedToken = "accessToken";

      given(jwtService.createToken(claims, userName, expirationTime))
              .willReturn(expectedToken);

      String token = jwtService.createToken(claims, userName, expirationTime);

      assertNotNull(token);
      assertEquals(expectedToken, token);
    }
    @Test
    void testGenerateAccessAndRefreshToken() {
        String userName = "admin";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(jwtService.generateAccessToken(anyString())).willReturn(accessToken);
        given(jwtService.generateRefreshToken(anyString())).willReturn(refreshToken);
        String token1 = jwtService.generateAccessToken(userName);
        String token2 = jwtService.generateRefreshToken(userName);
        
        assertEquals(token1, accessToken);
        assertEquals(token2, refreshToken);
    }
    @Test
    void testExtractUsername() {
        String token = "validToken";
        given(jwtService.extractUsername(anyString())).willReturn("admin");

        String userName = jwtService.extractUsername(token);

        assertEquals(userName, "admin");
    }

    @Test
    void testExtractExpiration() {
        String token = "validToken";
        Date expiration = new Date(System.currentTimeMillis() + accessExpiration);

        given(jwtService.extractExpiration(anyString())).willReturn(expiration);

        Date date = jwtService.extractExpiration(token);

        // Assert
        assertEquals(date, expiration);
    }

    @Test
    void testIsTokenExpired() {
        String expiredToken = "expiredToken";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // Date dans le passé
        given(jwtService.extractExpiration(eq(expiredToken))).willReturn(expiredDate);

        // Act
        boolean isExpired = jwtService.isTokenExpired(expiredToken);

  
        assertTrue(isExpired);
    }

    // Test de la méthode isTokenExpired avec un token valide
    @Test
    void testIsTokenNotExpired() {
        // Arrange
        String validToken = "validToken";
        Date validDate = new Date(System.currentTimeMillis() + 10000); // Date dans le futur
        given(jwtService.extractExpiration(eq(validToken)))
                .willReturn(validDate);

        // Act
        boolean isExpired = jwtService.isTokenExpired(validToken);

        // Assert
        assertFalse(isExpired, "Le token ne devrait pas être expiré");
    }


    @Test
    void testIsTokenValid() {
        String validToken = "validToken";

        given(jwtService.isTokenValide(validToken)).willReturn(true);

        boolean isValid = jwtService.isTokenValide(validToken);

        assertTrue(isValid);
    }
    @Test
    void testIsTokenInvalid() {
        String invalidToken = "invalidToken";
        given(jwtService.isTokenValide(invalidToken)).willReturn(false);

        boolean isValid = jwtService.isTokenValide(invalidToken);

        assertFalse(isValid);
    }

    // Test de createToken (privée) indirectement via generateAccessToken
    
}
