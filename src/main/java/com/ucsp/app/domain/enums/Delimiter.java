package com.ucsp.app.domain.enums;

public enum Delimiter {
  OPEN_PARENTHESIS("("),
  CLOSE_PARENTHESIS(")"),
  OPEN_BRACE("{"),
  CLOSE_BRACE("}");

  private final String value;

  Delimiter(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
