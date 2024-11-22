package com.ucsp.app.domain.scanner;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.processors.impl.CommentProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.impl.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scanner {
  private final List<TokenProcessor> tokenProcessors;

  private final List<Token> tokens;

  private final Reader reader;


  public Scanner(List<TokenProcessor> tokenProcessors, Reader reader) {
    this.tokenProcessors = tokenProcessors;
    this.tokens = new ArrayList<>();
    this.reader = reader;
  }

  private boolean isUnrecognizedCharacter(char currentChar) {
    return !Character.isWhitespace(currentChar) && currentChar != '\n';
  }

  public void tokenize() throws IOException {
    while (reader.hasNext()) {
      char currentChar = reader.peekChar();
      boolean isProcessed = false;
      for (TokenProcessor processor : tokenProcessors) {
        if (processor.supports(currentChar)) {
          isProcessed = true;
          var processedToken = processor.process();
          if (!(processor instanceof CommentProcessor && Objects.isNull(processedToken))) {
            // Whether the processed token is a comment.
            tokens.add(processedToken);
          }
          break;
        }
      }
      if (!isProcessed) {
        if (isUnrecognizedCharacter(currentChar)) Logger.error(String.valueOf(currentChar));
        Logger.updatePosition(reader.getChar());
      }
    }
    tokens.add(new Token(Category.EOF, "$"));
    reader.close();
  }

  public List<Token> getTokens() {
    return tokens;
  }
}
