package com.ucsp.app.domain.validators;

public interface CharacterValidator {
  boolean isLetter(char c);

  boolean isDigit(char c);

  boolean isWhitespace(char c);
}
