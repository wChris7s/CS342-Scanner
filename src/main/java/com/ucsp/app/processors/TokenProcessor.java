package com.ucsp.app.processors;

import com.ucsp.app.token.Token;

import java.io.IOException;

public interface TokenProcessor {
  Token process() throws IOException;

  boolean supports(char currentChar);
}
