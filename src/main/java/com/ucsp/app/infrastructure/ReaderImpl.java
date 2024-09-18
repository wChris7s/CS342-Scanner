package com.ucsp.app.infrastructure;

import com.ucsp.app.application.Reader;
import com.ucsp.app.domain.TokenProcessor;

import java.io.IOException;

public class ReaderImpl implements Reader {

  private final TokenProcessor tokenProcessor;

  public ReaderImpl(TokenProcessor tokenProcessor) {
    this.tokenProcessor = tokenProcessor;
  }

  @Override
  public void read() throws IOException {
    tokenProcessor.processTokens();
  }
}