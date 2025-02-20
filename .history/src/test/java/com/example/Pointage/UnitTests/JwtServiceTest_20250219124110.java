package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Pointage.Configuration.JwtService;

import io.jsonwebtoken.Jwts;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private long accessExpiration = 3600000;

    @Test
    public void testCreateToken() {
        // Préparer les données
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        String userName = "userTest";
        long duree = 3600000; // 1 heure en millisecondes

        // Mock de la méthode getSignKey
        SecretKey mockSecretKey = mock(SecretKey.class);
        when(tokenService.getSignKey()).thenReturn(mockSecretKey);

        // Génération du token avec la méthode mockée
        String token = tokenService.createToken(claims, userName, duree);

        // Vérifier que le token n'est pas nul
        assertNotNull(token);

        // Vérifier que la méthode getSignKey a bien été appelée
        verify(tokenService, times(1)).getSignKey();

        // Décoder le token pour valider son contenu
        String decodedToken = Jwts.parserBuilder()
                .setSigningKey(mockSecretKey)  // Utilisation de la clé mockée
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        // Vérifier le nom d'utilisateur dans le token
        assertEquals(userName, decodedToken);
    }
    
    @Test
    void testCreateTokenIndirectly() {
      String userName = "admin";
      Map<String, Object> claims = new HashMap<>();
      String expectedToken = "accessToken";

      given(jwtService.createToken(claims, userName, accessExpiration))
              .willReturn(expectedToken);

      String token = jwtService.createToken(claims, userName, accessExpiration);

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
