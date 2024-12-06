package com.ucsp.app.token.types.impl;

import com.ucsp.app.token.types.TokenType;

public enum Keyword implements TokenType {
  ARRAY("array"),
  BOOL("bool"),
  CHAR("char"),
  ELSE("else"),
  FALSE("false"),
  TRUE("true"),
  FOR("for"),
  FUNCTION("function"),
  IF("if"),
  INT("int"),
  PRINT("print"),
  RETURN("return"),
  STRING("string"),
  VOID("void"),
  WHILE("while");

  private final String value;

  Keyword(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static Keyword fromString(String value) {
    for (Keyword keyword : values()) {
      if (keyword.value.equals(value)) {
        return keyword;
      }
    }
    throw new IllegalArgumentException("No enum constant for value: " + value);
  }
}