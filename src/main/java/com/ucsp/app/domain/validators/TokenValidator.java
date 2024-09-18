package com.ucsp.app.domain.validators;

public interface TokenValidator {
  boolean isValidToken(char character);

  boolean isKeyword(String token);
}
