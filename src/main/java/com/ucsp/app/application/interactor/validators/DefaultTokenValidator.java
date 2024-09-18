package com.ucsp.app.application.interactor.validators;

import com.ucsp.app.domain.ReservedWord;
import com.ucsp.app.domain.validators.CharacterValidator;
import com.ucsp.app.domain.validators.TokenValidator;

import java.util.Arrays;

public class DefaultTokenValidator implements TokenValidator {

  private final CharacterValidator chrValidator;

  public DefaultTokenValidator(CharacterValidator chrValidator) {
    this.chrValidator = chrValidator;
  }

  @Override
  public boolean isValidToken(char character) {
    return chrValidator.isDigit(character)
      || chrValidator.isLetter(character)
      || character == '_';
  }

  @Override
  public boolean isKeyword(String token) {
    return Arrays.stream(ReservedWord.values())
      .map(ReservedWord::toString)
      .anyMatch(s -> s.equals(token));
  }
}
