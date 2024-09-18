package com.ucsp.app.domain;

import com.ucsp.app.application.ReservedWord;
import com.ucsp.app.application.Validator;

import java.util.Arrays;

public class CharacterValidator implements Validator {
  @Override
  public boolean isLetter(char c) {
    return Character.isLetter(c);
  }

  @Override
  public boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  @Override
  public boolean isKeyword(String token) {
    return Arrays.stream(ReservedWord.values())
      .map(ReservedWord::toString)
      .anyMatch(reservedWord -> reservedWord.equals(token));
  }
}
