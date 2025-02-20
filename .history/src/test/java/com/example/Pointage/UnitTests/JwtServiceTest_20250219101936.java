package com.example.Pointage.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.example.Pointage.Configuration.JwtService;

@ExtendWith(SpringExtension.class)

public class JwtServiceTest {
  @Mock
  private JwtService jwtService;
  

  @Test
  void testGenerateAccesAndRefreshToken(){

    given(jwtService.generateAccessToken(anyString())).willReturn("accessToken");
    
    String token = jwtService.generateAccessToken("username");

    assertEquals(token, "accessToken");
  }
}
