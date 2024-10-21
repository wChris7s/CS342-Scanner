package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Operator;

import java.io.IOException;

public class OperatorProcessor implements TokenProcessor {

  private final Reader reader;

  public OperatorProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = Logger.getColumn();
    tokenBuilder.append(reader.peekChar());
    Logger.updatePosition(reader.getChar());
    if (Operator.contains(tokenBuilder.toString() + reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      Logger.updatePosition(reader.getChar());
    }
    String processedToken = tokenBuilder.toString();
    Operator operator = Operator.fromString(processedToken);
    Logger.debug(operator, processedToken, currentColumn);
    return new Token(operator, processedToken);
  }

  @Override
  public boolean supports(char currentChar) {
    return Operator.contains(String.valueOf(currentChar));
  }
}
