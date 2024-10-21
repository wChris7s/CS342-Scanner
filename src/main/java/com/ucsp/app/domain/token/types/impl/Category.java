package com.ucsp.app.domain.token.types.impl;

import com.ucsp.app.domain.token.types.TokenType;

public enum Category implements TokenType {
  IDENTIFIER("id"),
  EOF("eof"),
  INT_LITERAL("int_literal"),
  CHAR_LITERAL("char_literal"),
  STRING_LITERAL("string_literal"),
  BOOL_LITERAL("bool_literal");

  private final String value;

  Category(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
