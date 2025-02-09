package com.example.Pointage.Configuration;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Pointage.MSC.CustomUserDetailServices;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired 
  private JwtService jwtService;
  @Autowired
  private CustomUserDetailServices userDetailServices;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
      String token = null;
      String username = null;

      try {
        token = getTokenFromRequest(request);
        if(token != null){
          username = jwtService.extractUsername(token);
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
          UserDetails userDetails = userDetailServices.loadUserByUsername(username);
          if(jwtService.isTokenValide(token)){
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      } catch (ExpiredJwtException e) {
        System.out.println("JWT token has expired" + e.getMessage());
      } catch (MalformedJwtException e) {
        System.out.println("Invalid JWT token" + e.getMessage());
      } catch (io.jsonwebtoken.security.SignatureException e) {
        System.out.println("Invalid JWT signature" + e.getMessage());
      } catch (Exception e) {
        System.out.println("An unexpected error occurred" + e.getMessage());
      }
    filterChain.doFilter(request, response);
  }
  
  private String getTokenFromRequest(HttpServletRequest request){
    String token = request.getHeader("Authorization");
    if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
        return token.substring(7, token.length());
    }
    return null;
  }
}
