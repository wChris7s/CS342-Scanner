package com.ucsp.app.application.port.in;

import java.io.IOException;

public interface ScannerUseCase {
  void read() throws IOException;
}