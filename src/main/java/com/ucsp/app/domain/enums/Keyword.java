package com.ucsp.app.domain.enums;

public enum Keyword {
  ARRAY("array"),
  BOOLEAN("boolean"),
  CHAR("char"),
  ELSE("else"),
  FALSE("false"),
  TRUE("true"),
  FOR("for"),
  FUNCTION("function"),
  IF("if"),
  INTEGER("integer"),
  PRINT("print"),
  RETURN("return"),
  STRING("string"),
  VOID("void"),
  WHILE("while");

  private final String word;

  Keyword(String word) {
    this.word = word;
  }

  @Override
  public String toString() {
    return this.word;
  }
}
