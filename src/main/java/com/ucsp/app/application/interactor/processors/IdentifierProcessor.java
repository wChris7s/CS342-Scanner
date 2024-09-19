package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.enums.Keyword;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class IdentifierProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  public IdentifierProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
  }

  private boolean isValidSequence(char c) {
    return Character.isLetter(c) || Character.isDigit(c) || c == '_';
  }

  public boolean isKeyword(String token) {
    return Arrays.stream(Keyword.values())
      .map(Keyword::toString)
      .anyMatch(s -> s.equals(token));
  }

  private String getLogMessage(String token) {
    if (isKeyword(token)) {
      return LogMessage.KEYWORD;
    }
    return LogMessage.IDENTIFIER;
  }

  @Override
  public void process() throws IOException {
    var token = new StringBuilder();
    int currentColumn = LogPosition.getColumn();
    while (readerManager.hasNext() && isValidSequence(readerManager.peekChar())) {
      token.append(readerManager.peekChar());
      LogPosition.updatePosition(readerManager.getChar());
    }
    log.debug(getLogMessage(token.toString()), token, LogPosition.getLine(), currentColumn);
  }

  @Override
  public boolean supports(char currentChar) {
    return Character.isLetter(currentChar) || currentChar == '_';
  }
}
