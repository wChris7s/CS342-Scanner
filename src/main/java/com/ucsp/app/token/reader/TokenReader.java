package com.ucsp.app.token.reader;

import com.ucsp.app.token.Token;

public interface TokenReader {
  Token getCurrentToken();

  void advanceToken();

  boolean hasMoreTokens();
}
