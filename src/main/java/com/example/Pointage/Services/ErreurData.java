package com.example.Pointage.Services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErreurData {
  private int statut;
  private String message;
}
