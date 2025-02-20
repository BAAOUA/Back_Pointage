import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService; // Le service à tester

    @Mock
    private JwtService jwtServiceMock; // Mock pour simuler les dépendances internes

    private String jwtSecret = "secretKey"; // Clé secrète pour les tests
    private long accessExpiration = 3600000; // 1 heure
    private long refreshExpiration = 7200000; // 2 heures

    @BeforeEach
    public void setUp() {
        // Initialisation des valeurs
        jwtService.jwtSecret = jwtSecret;
        jwtService.access = accessExpiration;
        jwtService.refresh = refreshExpiration;
    }

    // Test de la génération d'un token d'accès
    @Test
    void testGenerateAccessToken() {
        // Arrange
        String userName = "username";
        String expectedToken = "accessToken";

        // Stub de la méthode createToken pour retourner un token simulé
        given(jwtServiceMock.createToken(anyMap(), eq(userName), eq(accessExpiration)))
                .willReturn(expectedToken);

        // Act
        String actualToken = jwtService.generateAccessToken(userName);

        // Assert
        assertEquals(expectedToken, actualToken, "Le token d'accès généré doit correspondre à celui attendu");
    }

    // Test de la génération d'un token de rafraîchissement
    @Test
    void testGenerateRefreshToken() {
        // Arrange
        String userName = "username";
        String expectedToken = "refreshToken";

        // Stub de la méthode createToken pour retourner un token simulé
        given(jwtServiceMock.createToken(anyMap(), eq(userName), eq(refreshExpiration)))
                .willReturn(expectedToken);

        // Act
        String actualToken = jwtService.generateRefreshToken(userName);

        // Assert
        assertEquals(expectedToken, actualToken, "Le token de rafraîchissement généré doit correspondre à celui attendu");
    }

    // Test de la méthode extractUsername
    @Test
    void testExtractUsername() {
        // Arrange
        String token = "validToken";
        String expectedUserName = "username";

        // Stub de la méthode extractClaim pour renvoyer le nom d'utilisateur
        given(jwtServiceMock.extractClaim(eq(token), any()))
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
        given(jwtServiceMock.extractClaim(eq(token), any()))
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
        given(jwtServiceMock.extractExpiration(eq(expiredToken)))
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
        given(jwtServiceMock.extractExpiration(eq(validToken)))
                .willReturn(validDate);

        // Act
        boolean isExpired = jwtService.isTokenExpired(validToken);

        // Assert
        assertFalse(isExpired, "Le token ne devrait pas être expiré");
    }

    // Test de la méthode isTokenValide avec un token valide
    @Test
    void testIsTokenValid() {
        // Arrange
        String validToken = "validToken";

        // Stub de la méthode de validation du token
        given(jwtServiceMock.isTokenValide(validToken)).willReturn(true);

        // Act
        boolean isValid = jwtService.isTokenValide(validToken);

        // Assert
        assertTrue(isValid, "Le token devrait être valide");
    }

    // Test de la méthode isTokenValide avec un token invalide
    @Test
    void testIsTokenInvalid() {
        // Arrange
        String invalidToken = "invalidToken";

        // Stub de la méthode de validation du token
        given(jwtServiceMock.isTokenValide(invalidToken)).willReturn(false);

        // Act
        boolean isValid = jw
