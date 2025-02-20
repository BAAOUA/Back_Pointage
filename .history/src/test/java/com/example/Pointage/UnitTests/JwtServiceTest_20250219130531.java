package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Pointage.Configuration.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private JwtParserBuilder jwtParserBuilder;

    @Mock
    private JwtParser jwtParser;

    @Mock
    private Jws<Claims> jwsClaims;

    @Mock
    private Claims claims;

    private long tokenExpiration = 180000;

    @Test
    void testCreateTokenIndirectly() {
      String userName = "admin";
      Map<String, Object> claims = new HashMap<>();
      String expectedToken = "accessToken";

      given(jwtService.createToken(claims, userName, tokenExpiration))
              .willReturn(expectedToken);

      String token = jwtService.createToken(claims, userName, tokenExpiration);

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
    void extractAllClaims_ShouldReturnClaimsWhenTokenIsValid() {
        // Arrange
        String token = "validToken";

        // Mock the behavior of Jwts.parserBuilder()
        when(jwtParserBuilder.setSigningKey(jwtService.getSignKey())).thenReturn(jwtParserBuilder);
        when(jwtParserBuilder.build()).thenReturn(jwtParser);
        when(jwtParser.parseClaimsJws(token)).thenReturn(jwsClaims);
        when(jwsClaims.getBody()).thenReturn(claims);

        // Act
        Claims extractedClaims = jwtService.extractAllClaims(token);

        // Assert
        assertNotNull(extractedClaims);
        assertEquals(claims, extractedClaims);
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
        Date expiration = new Date(System.currentTimeMillis() + tokenExpiration);

        given(jwtService.extractExpiration(anyString())).willReturn(expiration);

        Date date = jwtService.extractExpiration(token);

        assertEquals(date, expiration);
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
    
}
