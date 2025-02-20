package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Date;

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
    assert
    assertEquals(accesstoken, "accessToken");
    assertEquals(refreshtoken, "refreshToken");
  }
  @Test
  void testExtractUsername(){

    given(jwtService.extractUsername(anyString())).willReturn("admin");
    
    String user = jwtService.extractUsername(anyString());

    assertEquals(user, "admin");
  }
}
