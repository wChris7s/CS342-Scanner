package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Delimiter;

import java.io.IOException;

public class DelimiterProcessor implements TokenProcessor {

  private final Reader reader;

  public DelimiterProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
  public Token process() throws IOException {
    int currentColumn = Logger.getColumn();
    char processedToken = reader.getChar();
    Logger.updatePosition(processedToken);
    Delimiter delimiter = Delimiter.fromString(String.valueOf(processedToken));
    Logger.debug(delimiter, delimiter.value(), currentColumn);
    return new Token(delimiter, delimiter.value());
  }

  @Override
  public boolean supports(char currentChar) {
    return Delimiter.contains(String.valueOf(currentChar));
  }
}
