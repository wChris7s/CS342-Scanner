package com.ucsp.app;

import com.ucsp.app.application.interactor.ScannerInteractor;
import com.ucsp.app.application.interactor.TokenProcessorInteractor;
import com.ucsp.app.application.interactor.processors.*;
import com.ucsp.app.application.port.in.ScannerUseCase;
import com.ucsp.app.application.port.in.TokenProcessorUseCase;
import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.token.TokenProcessor;
import com.ucsp.app.infrastructure.adapter.out.file.DefaultReaderManager;

import java.io.IOException;
import java.util.Arrays;

public class CsApplication {
  public static void main(String[] args) throws IOException {
    String path = "src/main/resources/files/test1.bminor";
    ReaderManager readerManager = new DefaultReaderManager(path);

    TokenProcessor identifierProcessor = new IdentifierProcessor(readerManager);
    TokenProcessor commentProcessor = new CommentProcessor(readerManager);
    TokenProcessor delimiterProcessor = new DelimiterProcessor(readerManager);
    TokenProcessor characterProcessor = new CharacterProcessor(readerManager);
    TokenProcessor operatorProcessor = new OperatorProcessor(readerManager);
    TokenProcessor stringProcessor = new StringProcessor(readerManager);

    TokenProcessorUseCase tokenProcessorUseCase = new TokenProcessorInteractor(Arrays.asList(identifierProcessor,
      commentProcessor, delimiterProcessor, characterProcessor, operatorProcessor, stringProcessor),
      readerManager);
    ScannerUseCase scannerUseCase = new ScannerInteractor(tokenProcessorUseCase);
    scannerUseCase.read();
  }
}