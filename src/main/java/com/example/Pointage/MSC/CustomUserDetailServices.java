package com.example.Pointage.MSC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Pointage.Services.LoginDAO;

@Service
public class CustomUserDetailServices implements UserDetailsService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) {
    if("admin".equals(username)){
      String pwdEncode = passwordEncoder.encode("admin123");
    return User.builder()
                .username("admin")
                .password(pwdEncode)
                .roles("ADMIN")
                .build();
    } 
    return null;
  }
  public boolean getAuthUser(LoginDAO login){
    return login.getUsername().equals("admin") && login.getPassword().equals("admin123");
}
}
