
package com.example.Pointage.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.Pointage.MSC.CustomUserDetailServices;

import static org.springframework.security.config.Customizer.withDefaults;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SpringSecurityConfig {
  @Autowired
  private JwtAuthentificationEntryPoint authEntryPoint;
  @Autowired
  private AccessDeniedHandlerException accessDenied;
  @Autowired
  private JwtAuthFilter jwtAuthFilter;

  @Bean
  public UserDetailsService userDetailsService() {
    return new CustomUserDetailServices(); // Ensure UserInfoService implements UserDetailsService
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
      http
              .cors(withDefaults())
              .csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> {
                  auth.requestMatchers("/auth/**").permitAll();
                  auth.requestMatchers(HttpMethod.GET,"/employees").hasAnyRole("ADMIN", "USER");
                  auth.requestMatchers(HttpMethod.POST,"/employees/add").hasRole("ADMIN");
                  auth.anyRequest().authenticated();
              });
      http.authenticationProvider(authenticationProvider());
      http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
      http.exceptionHandling(exception ->{
        exception.authenticationEntryPoint(authEntryPoint).accessDeniedHandler(accessDenied);
      });
    return http.build();
  }
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
  
  @Bean
  public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
    return configuration.getAuthenticationManager();
  }
}
