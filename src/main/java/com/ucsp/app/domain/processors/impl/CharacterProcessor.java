package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.AppLogger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;

import java.io.IOException;

public class CharacterProcessor implements TokenProcessor {

  private final Reader reader;

  public CharacterProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
    public Token process() throws IOException {
    int currentColumn = AppLogger.getColumn();
    AppLogger.updatePosition(reader.getChar());  // Consume la comilla simple inicial
    char character = reader.getChar();  // Extraemos el carácter
    AppLogger.updatePosition(character);
    AppLogger.updatePosition(reader.getChar());  // Consume la comilla simple final
    String literalValue = String.valueOf(character);
    AppLogger.debug(Category.CHAR_LITERAL, literalValue, currentColumn);
    return new Token(Category.CHAR_LITERAL, literalValue);
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '\'';
  }
}
