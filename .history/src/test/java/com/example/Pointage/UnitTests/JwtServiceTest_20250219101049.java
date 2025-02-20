package com.example.Pointage.UnitTests;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.Pointage.Configuration.JwtService;

@ExtendWith(SpringExtension.class)
@Au
public class JwtServiceTest {
  @InjectMocks
  private JwtService jwtService
}
