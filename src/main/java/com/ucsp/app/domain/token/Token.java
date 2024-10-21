package com.ucsp.app.domain.token;

import com.ucsp.app.domain.token.types.TokenType;

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
