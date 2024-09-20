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

  private void processBlockComment(boolean state) throws IOException {
    LogPosition.updatePosition(readerManager.getChar());
    while (readerManager.hasNext()) {
      char currentChar = readerManager.getChar(); // get '*' and move next '/'
      LogPosition.updatePosition(currentChar);
      if (currentChar == '*' && readerManager.hasNext() && readerManager.peekChar() == '/') {
        LogPosition.updatePosition(readerManager.getChar());
        break;
      }
    }
    if(state==true)
      log.debug(LogMessage.BLOCK_COMMENT_NF);
    // TODO: Handle comment without closure (*'/).
  }

  @Override
  public void process() throws IOException {
    if (readerManager.hasNext()) {
      LogPosition.updatePosition(readerManager.getChar());  // process the first '/'
      if (readerManager.peekChar() == '/') {
        processInlineComment();
      } else if (readerManager.peekChar() == '*') {
        boolean state=true;
        processBlockComment(state);
      }
    }
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '/';
  }
}
