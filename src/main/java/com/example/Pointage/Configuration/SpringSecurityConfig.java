
package com.example.Pointage.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.Pointage.MSC.CustomUserDetailServices;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfig {
  @Autowired
  private JwtAuthentificationEntryPoint authEntryPoint;
  @Autowired
  private JwtAuthFilter jwtAuthFilter;

  @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailServices(); // Ensure UserInfoService implements UserDetailsService
    }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http
        .cors().and()
        .csrf(csrf->csrf.disable())
        .authorizeHttpRequests(auth->{
          auth.requestMatchers("/auth/**").permitAll();
          //auth.requestMatchers("/employees/add").hasAuthority("ROLE_ADMIN");
          auth.anyRequest().authenticated();
        }).httpBasic(Customizer.withDefaults());
    http.authenticationProvider(authenticationProvider());
    http.exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint));
    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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
