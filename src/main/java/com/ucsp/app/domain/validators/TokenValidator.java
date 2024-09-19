package com.ucsp.app.domain.validators;

public interface TokenValidator {
  boolean isKeyword(String token);

  boolean isOperator(String token);

  boolean isDelimiter(String token);
}
