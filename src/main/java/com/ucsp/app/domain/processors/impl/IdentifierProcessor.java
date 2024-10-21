package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;
import com.ucsp.app.domain.token.types.impl.Keyword;

import java.io.IOException;

public class IdentifierProcessor implements TokenProcessor {

  private final Reader reader;

  public IdentifierProcessor(Reader reader) {
    this.reader = reader;
  }

  private boolean isValidSequence(char c) {
    return Character.isLetter(c) || Character.isDigit(c) || c == '_';
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = Logger.getColumn();
    while (reader.hasNext() && isValidSequence(reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      Logger.updatePosition(reader.getChar());
    }
    try {
      String processedToken = tokenBuilder.toString();
      Keyword keyword = Keyword.fromString(processedToken);
      Logger.debug(keyword, processedToken, currentColumn);
      return new Token(keyword, processedToken);
    } catch (IllegalArgumentException exception) {
      String processedToken = tokenBuilder.toString();
      Logger.debug(Category.IDENTIFIER, processedToken, currentColumn);
      return new Token(Category.IDENTIFIER, processedToken);
    }
  }

  @Override
  public boolean supports(char currentChar) {
    return Character.isLetter(currentChar) || currentChar == '_';
  }
}
