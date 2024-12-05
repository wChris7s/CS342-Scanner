package com.ucsp.app.domain.scanner;

import com.ucsp.app.domain.logger.ScannerPositionManager;
import com.ucsp.app.domain.logger.utils.LoggerMessage;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.processors.impl.CommentProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
public class Scanner {
  private final List<TokenProcessor> tokenProcessors;

  private final List<Token> tokens;

  private final Reader reader;

  private final ScannerPositionManager positionManager;

  public Scanner(List<TokenProcessor> tokenProcessors, Reader reader) {
    this.tokenProcessors = tokenProcessors;
    this.tokens = new ArrayList<>();
    this.reader = reader;
    this.positionManager = ScannerPositionManager.getInstance();
  }

  private boolean isUnrecognizedCharacter(char currentChar) {
    return !Character.isWhitespace(currentChar) && currentChar != '\n';
  }

  public void tokenize() throws IOException {
    while (reader.hasNext()) {
      char currentChar = reader.peekChar();
      if (!isProcessedToken(currentChar)) {
        if (isUnrecognizedCharacter(currentChar)) {
          log.error(LoggerMessage.SCANNER_ERROR,
              currentChar,
              positionManager.getLine(),
              positionManager.getColumn());
        }
        positionManager.updatePosition(reader.getChar());
      }
    }
    tokens.add(new Token(Category.EOF, "$"));
    reader.close();
  }

  private boolean isProcessedToken(char currentChar) throws IOException {
    boolean isProcessed = false;
    for (TokenProcessor processor : tokenProcessors) {
      if (processor.supports(currentChar)) {
        isProcessed = true;
        var processedToken = processor.process();
        if (!(processor instanceof CommentProcessor && Objects.isNull(processedToken))) {
          tokens.add(processedToken);
        }
        break;
      }
    }
    return isProcessed;
  }
}
