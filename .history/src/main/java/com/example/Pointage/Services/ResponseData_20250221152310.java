package com.example.Pointage.Services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {
  private String username;
  private String Role;
  private String accessToken;
  private String refreshToken;
}
