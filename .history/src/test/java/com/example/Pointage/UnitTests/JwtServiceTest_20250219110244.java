package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Pointage.Configuration.JwtService;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private String jwtSecret = "secretKey"; // Clé secrète pour les tests
    private long accessExpiration = 3600000; // 1 heure
    private long refreshExpiration = 7200000; // 2 heures

    // Test de la génération d'un token d'accès
    @Test
    void testGenerateAccessToken() {
        // Arrange
        String userName = "username";
        String expectedToken = "accessToken";

        given(jwtService.createToken(anyMap(), userName, anyLong()))
                .willReturn(expectedToken);
        given(jwtService.generateAccessToken(userName)).willReturn(expectedToken);
        String actualToken = jwtService.generateAccessToken(userName);

        // Assert
        assertEquals(expectedToken, actualToken);
    }

    // Test de la génération d'un token de rafraîchissement
    @Test
    void testGenerateRefreshToken() {
        // Arrange
        String userName = "username";
        String expectedToken = "refreshToken";

        // Stub de la méthode createToken pour retourner un token simulé
        given(jwtService.createToken(anyMap(), eq(userName), eq(refreshExpiration)))
                .willReturn(expectedToken);

        // Act
        String actualToken = jwtService.generateRefreshToken(userName);

        // Assert
        assertEquals(expectedToken, actualToken, "Le token de rafraîchissement généré doit correspondre à celui attendu");
    }


    @Test
    void testExtractUsername() {
        String token = "validToken";
        String expectedUserName = "username";


        given(jwtService.extractClaim(eq(token), any()))
                .willReturn(expectedUserName);

        // Act
        String userName = jwtService.extractUsername(token);

        // Assert
        assertEquals(expectedUserName, userName, "Le nom d'utilisateur extrait doit être correct");
    }

    // Test de la méthode extractExpiration
    @Test
    void testExtractExpiration() {
        // Arrange
        String token = "validToken";
        Date expectedExpiration = new Date(System.currentTimeMillis() + accessExpiration);

        // Stub de la méthode extractClaim pour renvoyer la date d'expiration
        given(jwtService.extractClaim(eq(token), any()))
                .willReturn(expectedExpiration);

        // Act
        Date expiration = jwtService.extractExpiration(token);

        // Assert
        assertEquals(expectedExpiration, expiration, "La date d'expiration extraite doit être correcte");
    }

    // Test de la méthode isTokenExpired avec un token expiré
    @Test
    void testIsTokenExpired() {
        // Arrange
        String expiredToken = "expiredToken";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // Date dans le passé
        given(jwtService.extractExpiration(eq(expiredToken)))
                .willReturn(expiredDate);

        // Act
        boolean isExpired = jwtService.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired, "Le token devrait être expiré");
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
    @Test
    void testCreateTokenIndirectly() {
        String userName = "username";
        String expectedToken = "accessToken";
        given(jwtService.createToken(anyMap(), eq(userName), anyLong()))
                .willReturn(expectedToken);

        String actualToken = jwtService.generateAccessToken(userName);

        assertNotNull(actualToken);
        assertTrue(actualToken.startsWith("eyJ"));
    }
}
