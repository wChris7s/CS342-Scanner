package com.ucsp.app.application.interactor;

import com.ucsp.app.application.port.in.TokenProcessorUseCase;
import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TokenProcessorInteractor implements TokenProcessorUseCase {
  private final List<TokenProcessor> processors;

  private final ReaderManager readerManager;

  public TokenProcessorInteractor(List<TokenProcessor> processors, ReaderManager readerManager) {
    this.processors = processors;
    this.readerManager = readerManager;
  }

  @Override
  public void processTokens() throws IOException {
    while (readerManager.hasNext()) {
      char currentChar = readerManager.peekChar();
      boolean processed = false;
      for (TokenProcessor processor : processors) {
        if (processor.supports(currentChar)) {
          processed = true;
          processor.process();
          break;
        }
      }
      if (!processed) {
        if (!Character.isWhitespace(currentChar) && currentChar != '\n') {
          log.error(LogMessage.UNRECOGNIZED_CHARACTER, currentChar, LogPosition.getLine(), LogPosition.getColumn());
        }
        LogPosition.updatePosition(readerManager.getChar());
      }
    }
    readerManager.close();
  }
}
