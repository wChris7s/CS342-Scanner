package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.types.impl.Category;
import com.ucsp.app.domain.token.types.impl.Keyword;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.processors.TokenProcessor;

import java.io.IOException;

public class StringProcessor implements TokenProcessor {

  private final Reader reader;

  public StringProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = Logger.getColumn();
    Logger.updatePosition(reader.getChar());
    while (reader.hasNext()) {
      char currentChar = reader.getChar();
      Logger.updatePosition(currentChar);
      if (currentChar == '"') break;
      tokenBuilder.append(currentChar);
    }
    String literalValue = tokenBuilder.toString();
    Logger.debug(Category.STRING_LITERAL, literalValue, currentColumn);
    return new Token(Category.STRING_LITERAL, literalValue);
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '"';
  }
}
