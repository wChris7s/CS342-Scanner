package com.ucsp.app.application.interactor;

import com.ucsp.app.application.port.in.TokenProcessorUseCase;
import com.ucsp.app.domain.manager.ReaderManager;
import com.ucsp.app.domain.validators.CharacterValidator;
import com.ucsp.app.domain.validators.TokenValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TokenProcessorInteractor implements TokenProcessorUseCase {

  private final CharacterValidator chValidator;

  private final TokenValidator tokenValidator;

  private final ReaderManager readerManager;

  public TokenProcessorInteractor(CharacterValidator chValidator, TokenValidator tokenValidator, ReaderManager readerManager) {
    this.chValidator = chValidator;
    this.tokenValidator = tokenValidator;
    this.readerManager = readerManager;
  }

  private boolean isValidToken(char c) {
    return chValidator.isLetter(c) || chValidator.isDigit(c) || c == '_';
  }

  private String getToken() throws IOException {
    StringBuilder token = new StringBuilder();
    while (readerManager.hasNext() && isValidToken(readerManager.peekChar())) {
      token.append(readerManager.getChar());
    }
    return token.toString();
  }

  private void processIdentifierOrKeyword() throws IOException {
    String token = getToken();
    if (tokenValidator.isKeyword(token)) {
      log.info("Reserved keyword: {}", token);
    } else {
      log.info("Identifier: {}", token);
    }
  }

  private void processComment() throws IOException {
    if (readerManager.hasNext()) {
      readerManager.getChar();
      char nextChar = readerManager.peekChar();
      if (nextChar == '/') {
        log.info("Inline comment");
        readerManager.getChar();
        while (readerManager.hasNext() && readerManager.peekChar() != '\n') {
          readerManager.getChar();
        }
      } else if (nextChar == '*') {
        log.info("Block comment");
        readerManager.getChar();
        boolean endOfBlockCommentFound = false;
        while (readerManager.hasNext()) {
          // Gets the current character temporarily and moves the pointer to the next one, the
          // temporary character is compared with the new current one.
          char currentChar = readerManager.getChar();
          if (currentChar == '*' && readerManager.hasNext() && readerManager.peekChar() == '/') {
            readerManager.getChar();
            endOfBlockCommentFound = true;
            break;
          }
        }
        if (!endOfBlockCommentFound) {
          log.error("End of block comment not found");
        }
      }
    }
  }

  private void processString() throws IOException {
    readerManager.getChar(); // Consume opening double quote
    StringBuilder string = new StringBuilder();
    while (readerManager.hasNext()) {
      char character = readerManager.getChar();
      if (character == '"') {
        break;
      }
      string.append(character);
    }
    log.info("String: {}", string);
  }


  private void processCharacter() throws IOException {
    readerManager.getChar();
    char character = readerManager.getChar();
    if (readerManager.getChar() == '\'') {
      log.info("Character: {}", character);
    } else {
      log.error("Invalid character literal");
    }
  }

  private void processOperator() throws IOException {
    StringBuilder operator = new StringBuilder();
    boolean isOperator = false;
    while (readerManager.hasNext() && !chValidator.isWhitespace(readerManager.peekChar())) {
      operator.append(readerManager.getChar());
      if (tokenValidator.isOperator(operator.toString())) {
        isOperator = true;
        log.info("Operator: {}", operator);
      }
    }
    if (!isOperator) {
      readerManager.getChar();
    }
  }

  private void processDelimiter() throws IOException {
    char delimiter = readerManager.getChar();
    log.info("Delimiter: {}", delimiter);
  }

  @Override
  public void processTokens() throws IOException {
    while (readerManager.hasNext()) {
      char currentChar = readerManager.peekChar();
      if (chValidator.isLetter(currentChar) || currentChar == '_') {
        processIdentifierOrKeyword();
      } else if (currentChar == '/') {
        processComment();
      } else if (currentChar == '\'') {
        processCharacter();
      } else if (currentChar == '"') {
        processString();
      } else if (currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == '[' || currentChar == ']') {
        processDelimiter();
      } else {
        processOperator();
      }
    }
    readerManager.close();
  }
}
