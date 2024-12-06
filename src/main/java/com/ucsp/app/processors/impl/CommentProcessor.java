package com.ucsp.app.processors.impl;

import com.ucsp.app.logger.ScannerPositionManager;
import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.processors.TokenProcessor;
import com.ucsp.app.reader.Reader;
import com.ucsp.app.token.Token;
import com.ucsp.app.token.types.impl.Operator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CommentProcessor implements TokenProcessor {

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public CommentProcessor(Reader reader) {
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  private void processInlineComment() throws IOException {
    while (reader.hasNext() && reader.peekChar() != '\n')
      positionManager.updatePosition(reader.getChar());
  }

  private void processBlockComment() throws IOException {
    boolean isClosed = false;
    positionManager.updatePosition(reader.getChar());
    while (reader.hasNext()) {
      char currentChar = reader.getChar(); // get '*' and move next '/'
      positionManager.updatePosition(currentChar);
      if (currentChar == '*' && reader.hasNext() && reader.peekChar() == '/') {
        isClosed = true;
        positionManager.updatePosition(reader.getChar());
        break;
      }
    }
    if (!isClosed) {
      throw new RuntimeException();
    }
  }

  @Override
  public Token process() throws IOException {
    if (reader.hasNext()) {
      char currentChar = reader.getChar();  // process the first '/'
      int currentColumn = positionManager.getColumn();
      positionManager.updatePosition(currentChar);
      if (reader.peekChar() == '/') {
        processInlineComment();
      } else if (reader.peekChar() == '*') {
        processBlockComment();
      } else {
        // Stop processing the comment to process the division operator.
        log.debug(LoggerMessage.SCANNER_DEBUG,
            Operator.DIVISION.name(),
            Operator.DIVISION.value(),
            positionManager.getLine(),
            currentColumn);

        return new Token(Operator.DIVISION, Operator.DIVISION.value());
      }
    }
    return null;
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '/';
  }
}
