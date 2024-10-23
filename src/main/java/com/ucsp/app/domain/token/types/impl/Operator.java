package com.ucsp.app.domain.token.types.impl;

import com.ucsp.app.domain.token.types.TokenType;

public enum Operator implements TokenType {
  INCREMENT("++"),
  DECREMENT("--"),
  SQUARE("**"),
  LOGICAL_NOT("!"),
  EXPONENTIATION("^"),
  MULTIPLICATION("*"),
  DIVISION("/"),
  MODULUS("%"),
  ADDITION("+"),
  SUBTRACTION("-"),
  LESS_THAN("<"),
  LESS_THAN_OR_EQUAL("<="),
  GREATER_THAN(">"),
  GREATER_THAN_OR_EQUAL(">="),
  EQUAL("=="),
  NOT_EQUAL("!="),
  AND("&&"),
  OR("||"),
  ASSIGNMENT("=");

  private final String value;

  Operator(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static Operator fromString(String value) {
    for (Operator operator : values()) {
      if (operator.value.equals(value)) {
        return operator;
      }
    }
    throw new IllegalArgumentException("No enum constant for value: " + value);
  }

  public static boolean contains(String currentChar) {
    for (Operator operator : values()) {
      if (operator.value.contains(currentChar)) {
        return true;
      }
    }
    return false;
  }
}
