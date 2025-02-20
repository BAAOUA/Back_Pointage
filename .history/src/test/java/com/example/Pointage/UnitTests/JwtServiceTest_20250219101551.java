package com.example.Pointage.UnitTests;

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
@AutoConfigureMockMvc
public class JwtServiceTest {
  @InjectMocks
  private JwtService jwtService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void testGenerateAccesAndRefreshToken(){

    given(jwtService.generateAccessToken(anyString())).willReturn("accessToken")
  }
}
