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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Pointage.Configuration.JwtService;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private String jwtSecret = "secretKey";
    private long accessExpiration = 3600000;
    private long refreshExpiration = 7200000;

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

        assertEquals(date, expiration);
    }

    @Test
    public void testIsTokenValid_WhenExceptionThrown_ShouldReturnFalse() {
        // Simuler le comportement de Jwts.parserBuilder()
        Jwts.JwtsParserBuilder mockParserBuilder = Mockito.mock(JwtsParserBuilder.class);
        when(mockParserBuilder.setSigningKey(Mockito.any())).thenReturn(mockParserBuilder);
        when(mockParserBuilder.build()).thenReturn(Mockito.mock(JwtsParser.class));
        when(mockParserBuilder.build().parseClaimsJws(Mockito.anyString())).thenThrow(SignatureException.class);

        // Remplacer la méthode getSignKey() par une valeur factice
        String fakeToken = "fakeToken";

        // Appeler la méthode à tester
        boolean result = jwtService.isTokenValide(fakeToken);

        // Vérifier que le résultat est false
        assertFalse(result);
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
