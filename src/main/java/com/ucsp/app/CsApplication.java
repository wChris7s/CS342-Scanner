package com.ucsp.app;

import com.ucsp.app.application.Reader;
import com.ucsp.app.domain.CharacterValidator;
import com.ucsp.app.domain.TokenProcessor;
import com.ucsp.app.infrastructure.CharacterReader;
import com.ucsp.app.infrastructure.ReaderImpl;

import java.io.IOException;

public class CsApplication {
  public static void main(String[] args) throws IOException {
    String path = "src/main/resources/files/test1.bminor";
    TokenProcessor tokenProcessor = new TokenProcessor(new CharacterValidator(), new CharacterReader(path));
    Reader reader = new ReaderImpl(tokenProcessor);
    reader.read();
  }
}