package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Operator;

import java.io.IOException;

public class CommentProcessor implements TokenProcessor {

  private final Reader reader;

  public CommentProcessor(Reader reader) {
    this.reader = reader;
  }

  private void processInlineComment() throws IOException {
    while (reader.hasNext() && reader.peekChar() != '\n')
      Logger.updatePosition(reader.getChar());
  }

  private void processBlockComment() throws IOException {
    boolean isClosed = false;
    Logger.updatePosition(reader.getChar());
    while (reader.hasNext()) {
      char currentChar = reader.getChar(); // get '*' and move next '/'
      Logger.updatePosition(currentChar);
      if (currentChar == '*' && reader.hasNext() && reader.peekChar() == '/') {
        isClosed = true;
        Logger.updatePosition(reader.getChar());
        break;
      }
    }
    if (!isClosed) throw new RuntimeException();
  }

  @Override
  public Token process() throws IOException {
    if (reader.hasNext()) {
      char currentChar = reader.getChar();  // process the first '/'
      int currentColumn = Logger.getColumn();
      Logger.updatePosition(currentChar);
      if (reader.peekChar() == '/') processInlineComment();
      else if (reader.peekChar() == '*') processBlockComment();
      else {  // stop processing the comment to process the division operator
        Logger.debug(Operator.DIVISION, Operator.DIVISION.value(), currentColumn);
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
