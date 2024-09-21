package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CommentProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  public CommentProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
  }

  private void processInlineComment() throws IOException {
    while (readerManager.hasNext() && readerManager.peekChar() != '\n') {
      LogPosition.updatePosition(readerManager.getChar());
    }
  }

  private void processBlockComment() throws IOException {
    boolean isClosed = false;
    LogPosition.updatePosition(readerManager.getChar());
    while (readerManager.hasNext()) {
      char currentChar = readerManager.getChar(); // get '*' and move next '/'
      LogPosition.updatePosition(currentChar);
      if (currentChar == '*' && readerManager.hasNext() && readerManager.peekChar() == '/') {
        isClosed = true;
        LogPosition.updatePosition(readerManager.getChar());
        break;
      }
    }
    if (!isClosed) {
      log.debug(LogMessage.BLOCK_COMMENT_ERR);
    }
  }

  @Override
  public void process() throws IOException {
    if (readerManager.hasNext()) {
      char currentChar = readerManager.getChar();  // process the first '/'
      int currentColumn = LogPosition.getColumn();
      LogPosition.updatePosition(currentChar);
      if (readerManager.peekChar() == '/') {
        processInlineComment();
      } else if (readerManager.peekChar() == '*') {
        processBlockComment();
      } else {  // stop processing the comment to process the division operator
        log.debug(LogMessage.OPERATOR, currentChar, LogPosition.getLine(), currentColumn);
      }
    }
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '/';
  }
}
