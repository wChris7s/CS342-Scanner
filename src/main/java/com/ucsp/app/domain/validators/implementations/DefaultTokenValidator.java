package com.ucsp.app.domain.validators.implementations;

import com.ucsp.app.domain.enums.Operator;
import com.ucsp.app.domain.enums.Keyword;
import com.ucsp.app.domain.validators.TokenValidator;

import java.util.Arrays;

public class DefaultTokenValidator implements TokenValidator {
  @Override
  public boolean isKeyword(String token) {
    return Arrays.stream(Keyword.values())
      .map(Keyword::toString)
      .anyMatch(s -> s.equals(token));
  }

  @Override
  public boolean isOperator(String token) {
    return Arrays.stream(Operator.values())
      .map(Operator::toString)
      .anyMatch(s -> s.equals(token));
  }

  @Override
  public boolean isDelimiter(String token) {
    return Arrays.stream(Operator.values())
      .map(Operator::toString)
      .anyMatch(s -> s.equals(token));
  }
}
