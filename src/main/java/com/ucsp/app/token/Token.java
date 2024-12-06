package com.ucsp.app.token;

import com.ucsp.app.token.types.TokenType;

public record Token(TokenType tokenType, String tokenValue) {

  @Override
  public TokenType tokenType() {
    return tokenType;
  }

  @Override
  public String tokenValue() {
    return tokenValue;
  }

  @Override
  public String toString() {
    return "Token(" + tokenType.name() + ", \"" + tokenValue + "\")";
  }
}
