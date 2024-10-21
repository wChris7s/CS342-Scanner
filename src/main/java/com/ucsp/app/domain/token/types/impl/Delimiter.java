package com.ucsp.app.domain.token.types.impl;

import com.ucsp.app.domain.token.types.TokenType;

public enum Delimiter implements TokenType {
  L_BRACE("{"),
  R_BRACE("}"),
  L_PARENTHESIS("("),
  R_PARENTHESIS(")"),
  SEMICOLON(";"),
  COMMA(",");

  private final String value;

  Delimiter(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static Delimiter fromString(String value) {
    for (Delimiter delimiter : values()) {
      if (delimiter.value.equals(value)) {
        return delimiter;
      }
    }
    throw new IllegalArgumentException("No enum constant for value: " + value);
  }

  public static boolean contains(String currentChar) {
    for (Delimiter delimiter : values()) {
      if (delimiter.value.equals(currentChar)) {
        return true;
      }
    }
    return false;
  }
}
