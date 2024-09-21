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
import java.util.List;

public class CsApplication {
  public static void main(String[] args) throws IOException {
    String path = "src/main/resources/files/test1.bminor";
    ReaderManager readerManager = new DefaultReaderManager(path);
    List<TokenProcessor> processors = List.of(
      new IdentifierProcessor(readerManager),
      new CommentProcessor(readerManager),
      new DelimiterProcessor(readerManager),
      new CharacterProcessor(readerManager),
      new OperatorProcessor(readerManager),
      new StringProcessor(readerManager),
      new IntegerProcessor(readerManager)
    );

    TokenProcessorUseCase tokenProcessorUseCase = new TokenProcessorInteractor(processors, readerManager);
    ScannerUseCase scannerUseCase = new ScannerInteractor(tokenProcessorUseCase);
    scannerUseCase.read();
  }
}