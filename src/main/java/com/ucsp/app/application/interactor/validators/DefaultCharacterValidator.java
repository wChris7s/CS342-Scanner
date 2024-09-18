package com.ucsp.app.application.interactor.validators;

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
}
