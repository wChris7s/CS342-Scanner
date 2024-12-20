package com.ucsp.app.scanner.processors.impl;

import com.ucsp.app.logger.ScannerPositionManager;
import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.reader.Reader;
import com.ucsp.app.scanner.processors.TokenProcessor;
import com.ucsp.app.token.Token;
import com.ucsp.app.token.types.impl.Operator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class OperatorProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public OperatorProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  @Override
  public Token process() throws IOException {
    StringBuilder tokenBuilder = new StringBuilder();
    int currentColumn = positionManager.getColumn();
    tokenBuilder.append(reader.peekChar());
    positionManager.updatePosition(reader.getChar());

    if (Operator.contains(tokenBuilder.toString() + reader.peekChar())) {
      tokenBuilder.append(reader.peekChar());
      positionManager.updatePosition(reader.getChar());
    }
    String processedToken = tokenBuilder.toString();
    Operator operator = Operator.fromString(processedToken);

    log.info(LoggerMessage.SCANNER_DEBUG,
        operator.name(),
        processedToken,
        positionManager.getLine(),
        currentColumn);

    return new Token(operator, processedToken);
  }

  @Override
  public boolean supports(char currentChar) {
    return Operator.contains(String.valueOf(currentChar));
  }
}
