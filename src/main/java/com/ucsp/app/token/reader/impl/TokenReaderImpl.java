package com.ucsp.app.token.reader.impl;

import com.ucsp.app.token.Token;
import com.ucsp.app.token.reader.TokenReader;

import java.util.List;

public class TokenReaderImpl implements TokenReader {
  private final List<Token> tokens;
  private int currentTokenIndex;

  public TokenReaderImpl(List<Token> tokens) {
    this.tokens = tokens;
    this.currentTokenIndex = 0;
  }

  @Override
  public Token getCurrentToken() {
    if (currentTokenIndex < tokens.size()) {
      return tokens.get(currentTokenIndex);
    }
    return null;
  }

  @Override
  public void advanceToken() {
    currentTokenIndex++;
  }

  @Override
  public boolean hasMoreTokens() {
    return currentTokenIndex < tokens.size();
  }
}
