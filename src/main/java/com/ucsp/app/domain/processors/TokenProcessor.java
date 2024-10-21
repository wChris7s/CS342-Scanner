package com.ucsp.app.domain.processors;

import com.ucsp.app.domain.token.Token;

import java.io.IOException;

public interface TokenProcessor {
  Token process() throws IOException;

  boolean supports(char currentChar);
}
