package com.ucsp.app.scanner.processors.impl;

import com.ucsp.app.logger.ScannerPositionManager;
import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.scanner.processors.TokenProcessor;
import com.ucsp.app.reader.Reader;
import com.ucsp.app.token.Token;
import com.ucsp.app.token.types.impl.Category;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CharacterProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public CharacterProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  @Override
  public Token process() throws IOException {
    int currentColumn = positionManager.getColumn();
    positionManager.updatePosition(reader.getChar());
    char character = reader.getChar();
    positionManager.updatePosition(character);
    positionManager.updatePosition(reader.getChar());

    log.info(LoggerMessage.SCANNER_DEBUG,
        Category.CHAR_LITERAL.name(),
        character,
        positionManager.getLine(),
        currentColumn);

    return new Token(Category.CHAR_LITERAL, String.valueOf(character));
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '\'';
  }
}