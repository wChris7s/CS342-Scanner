package com.ucsp.app.processors.impl;

import com.ucsp.app.logger.ScannerPositionManager;
import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.processors.TokenProcessor;
import com.ucsp.app.reader.Reader;
import com.ucsp.app.token.Token;
import com.ucsp.app.token.types.impl.Delimiter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DelimiterProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public DelimiterProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  @Override
  public Token process() throws IOException {
    int currentColumn = positionManager.getColumn();
    char processedToken = reader.getChar();
    positionManager.updatePosition(processedToken);
    Delimiter delimiter = Delimiter.fromString(String.valueOf(processedToken));

    log.debug(LoggerMessage.SCANNER_DEBUG,
        delimiter.name(),
        delimiter.value(),
        positionManager.getLine(),
        currentColumn);

    return new Token(delimiter, delimiter.value());
  }

  @Override
  public boolean supports(char currentChar) {
    return Delimiter.contains(String.valueOf(currentChar));
  }
}
