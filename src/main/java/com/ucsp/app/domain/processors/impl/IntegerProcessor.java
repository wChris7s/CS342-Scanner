package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.AppLogger;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;

import java.io.IOException;

public class IntegerProcessor implements TokenProcessor {

  private final Reader reader;

  public IntegerProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = AppLogger.getColumn();

    while (reader.hasNext() && Character.isDigit(reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      AppLogger.updatePosition(reader.getChar());
    }

    String literalValue = tokenBuilder.toString();
    AppLogger.debug(Category.INT_LITERAL, literalValue, currentColumn);
    return new Token(Category.INT_LITERAL, literalValue);  // Especificamos INT_LITERAL
  }

  @Override
  public boolean supports(char currentChar) {
    return Character.isDigit(currentChar);  // Maneja solo literales numéricos
  }
}
