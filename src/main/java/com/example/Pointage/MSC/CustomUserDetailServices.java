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
    
    String pwdEncode;
    if(username.equals("admin")){
      pwdEncode = passwordEncoder.encode("admin123");
      return User.builder()
                .username(username)
                .password(pwdEncode)
                .roles("ADMIN")
                .build();
    }
    if(username.equals("brahim")){
      pwdEncode = passwordEncoder.encode("brahim123");
      return User.builder()
                .username(username)
                .password(pwdEncode)
                .roles("USER")
                .build();
    }
    return null; 
  }
  public boolean getAuthUser(LoginDAO login){
    boolean admin = login.getUsername().equals("admin") && login.getPassword().equals("admin123");
    boolean user = login.getUsername().equals("brahim") && login.getPassword().equals("brahim123");

    
    return admin || user;
}
}
