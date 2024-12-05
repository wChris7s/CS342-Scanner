package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.ScannerPositionManager;
import com.ucsp.app.domain.logger.utils.LoggerMessage;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.types.impl.Category;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.processors.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StringProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public StringProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = positionManager.getColumn();
    positionManager.updatePosition(reader.getChar());

    while (reader.hasNext()) {
      char currentChar = reader.getChar();
      positionManager.updatePosition(currentChar);
      if (currentChar == '"') break;
      tokenBuilder.append(currentChar);
    }
    String literalValue = tokenBuilder.toString();

    log.debug(LoggerMessage.SCANNER_DEBUG,
        Category.STRING_LITERAL.name(),
        literalValue,
        positionManager.getLine(),
        currentColumn);

    return new Token(Category.STRING_LITERAL, literalValue);
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '"';
  }
}
