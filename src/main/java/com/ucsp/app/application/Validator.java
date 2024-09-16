package com.ucsp.app.application;

public interface Validator {
  boolean isLetter(char c);
  boolean isDigit(char c);
  boolean isKeyword(String token);
}
