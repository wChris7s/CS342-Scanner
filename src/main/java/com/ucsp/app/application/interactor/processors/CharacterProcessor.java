package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CharacterProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  public CharacterProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
  }

  @Override
  public void process() throws IOException {
    int currentColumn = LogPosition.getColumn();
    LogPosition.updatePosition(readerManager.getChar());
    char character = readerManager.getChar();
    LogPosition.updatePosition(character);
    if (readerManager.getChar() == '\'') {
      LogPosition.updatePosition(readerManager.peekChar());
      log.debug(LogMessage.CHAR, character, LogPosition.getLine(), currentColumn);
    }
    // TODO: Handles the character without closure (') or if it has more than one character.
  }

  @Override
  public boolean supports(char currentChar) {
    return currentChar == '\'';
  }
}
