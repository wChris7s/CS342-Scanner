package com.ucsp.app.application.interactor;

import com.ucsp.app.application.port.in.ScannerUseCase;
import com.ucsp.app.application.port.in.TokenProcessorUseCase;

import java.io.IOException;

public class ScannerInteractor implements ScannerUseCase {

  private final TokenProcessorUseCase tokenProcessorUseCase;

  public ScannerInteractor(TokenProcessorUseCase tokenProcessorUseCase) {
    this.tokenProcessorUseCase = tokenProcessorUseCase;
  }

  @Override
  public void read() throws IOException {
    tokenProcessorUseCase.processTokens();
  }
}
