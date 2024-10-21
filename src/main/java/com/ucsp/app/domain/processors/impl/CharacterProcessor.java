package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;
import com.ucsp.app.domain.token.types.impl.Keyword;

import java.io.IOException;

public class CharacterProcessor implements TokenProcessor {

  private final Reader reader;

  public CharacterProcessor(Reader reader) {
    this.reader = reader;
  }

  @Override
    public Token process() throws IOException {
    int currentColumn = Logger.getColumn();
    Logger.updatePosition(reader.getChar());  // Consume la comilla simple inicial
    char character = reader.getChar();  // Extraemos el carácter
    Logger.updatePosition(character);
    Logger.updatePosition(reader.getChar());  // Consume la comilla simple final
    String literalValue = String.valueOf(character);
    Logger.debug(Category.CHAR_LITERAL, literalValue, currentColumn);
    return new Token(Category.CHAR_LITERAL, literalValue);
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '\'';
  }
}
