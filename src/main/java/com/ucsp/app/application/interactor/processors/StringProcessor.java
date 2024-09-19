package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class StringProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  public StringProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
  }

  @Override
  public void process() throws IOException {
    var string = new StringBuilder();
    int currentColumn = LogPosition.getColumn();
    LogPosition.updatePosition(readerManager.getChar());
    while (readerManager.hasNext()) {
      char currentChar = readerManager.getChar();
      LogPosition.updatePosition(currentChar);
      if (currentChar == '"') {
        log.debug(LogMessage.STRING, string, LogPosition.getLine(), currentColumn);
        break;
      }
      string.append(currentChar);
    }
    // TODO: Handle string without closure (").
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '"';
  }
}
