package com.ucsp.app.domain.logger;

import lombok.Getter;

@Getter
public class ScannerPositionManager {

  private int line;

  private int column;

  private static ScannerPositionManager instance;

  private ScannerPositionManager() {
    this.line = 1;
    this.column = 1;
  }

  public static ScannerPositionManager getInstance() {
    if (instance == null) {
      instance = new ScannerPositionManager();
    }
    return instance;
  }

  public void updatePosition(char c) {
    if (c == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
  }
}