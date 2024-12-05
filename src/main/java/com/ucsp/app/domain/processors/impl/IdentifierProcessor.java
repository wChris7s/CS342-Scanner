package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.ScannerPositionManager;
import com.ucsp.app.domain.logger.utils.LoggerMessage;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;
import com.ucsp.app.domain.token.types.impl.Keyword;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class IdentifierProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public IdentifierProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  private boolean isValidSequence(char c) {
    return Character.isLetter(c) || Character.isDigit(c) || c == '_';
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = positionManager.getColumn();

    while (reader.hasNext() && isValidSequence(reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      positionManager.updatePosition(reader.getChar());
    }

    try {
      String processedToken = tokenBuilder.toString();
      Keyword keyword = Keyword.fromString(processedToken);

      log.debug(LoggerMessage.SCANNER_DEBUG,
          keyword.name(),
          keyword.value(),
          positionManager.getLine(),
          currentColumn);

      return new Token(keyword, processedToken);
    } catch (IllegalArgumentException exception) {
      String processedToken = tokenBuilder.toString();

      log.debug(LoggerMessage.SCANNER_DEBUG,
          Category.IDENTIFIER.name(),
          processedToken,
          positionManager.getLine(),
          currentColumn);

      return new Token(Category.IDENTIFIER, processedToken);
    }
  }

  @Override
  public boolean supports(char currentChar) {
    return Character.isLetter(currentChar) || currentChar == '_';
  }
}
