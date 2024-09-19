package com.ucsp.app.domain.enums;

public enum Operator {
  INCREMENT("++"),
  DECREMENT("--"),
  UNARY_NEGATION("-"),
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

  @Override
  public String toString() {
    return this.value;
  }
}
