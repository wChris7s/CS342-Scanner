package com.ucsp.app.domain.token;

import java.io.IOException;

public interface TokenProcessor {
  void process() throws IOException;

  boolean supports(char currentChar);
}
