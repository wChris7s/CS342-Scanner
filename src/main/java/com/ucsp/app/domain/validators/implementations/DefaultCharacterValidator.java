package com.ucsp.app.domain.validators.implementations;

import com.ucsp.app.domain.validators.CharacterValidator;

public class DefaultCharacterValidator implements CharacterValidator {
  @Override
  public boolean isLetter(char c) {
    return Character.isLetter(c);
  }

  @Override
  public boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  @Override
  public boolean isWhitespace(char c) {
    return Character.isWhitespace(c);
  }
}
