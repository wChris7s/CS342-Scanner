package com.ucsp.app.domain.log;

import lombok.Getter;

public class LogPosition {
  @Getter
  private static int line = 1;

  @Getter
  private static int column = 1;

  public static void updatePosition(char c) {
    if (c == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
  }
}
