package com.example.Pointage.Configuration;

import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthentificationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    System.out.println("exception_______________________________________________________d__d_d___d");
    
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(authException.getMessage());
  }
  
}
