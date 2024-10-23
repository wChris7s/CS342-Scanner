package com.ucsp.app.domain.token.reader;

import com.ucsp.app.domain.token.Token;

public interface TokenReader {
  Token getCurrentToken();

  void advanceToken();

  boolean hasMoreTokens();
}
