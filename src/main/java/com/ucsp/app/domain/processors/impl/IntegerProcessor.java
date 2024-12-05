package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.ScannerPositionManager;
import com.ucsp.app.domain.logger.utils.LoggerMessage;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class IntegerProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public IntegerProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = positionManager.getColumn();

    while (reader.hasNext() && Character.isDigit(reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      positionManager.updatePosition(reader.getChar());
    }

    String literalValue = tokenBuilder.toString();

    log.debug(LoggerMessage.SCANNER_DEBUG,
        Category.INT_LITERAL.name(),
        literalValue,
        positionManager.getLine(),
        currentColumn);

    return new Token(Category.INT_LITERAL, literalValue);
  }

  @Override
  public boolean supports(char currentChar) {
    return Character.isDigit(currentChar);
  }
}
