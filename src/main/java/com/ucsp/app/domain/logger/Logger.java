package com.ucsp.app.domain.logger;

import com.ucsp.app.domain.token.types.TokenType;

public class Logger {

  private static int line = 1;

  private static int column = 1;

  private static final String DEBUG_MESSAGE = "DEBUG SCAN - %s [ '%s' ] found at (%d:%d)";

  private static final String ERROR_MESSAGE = "ERROR SCAN - UNRECOGNIZED CHARACTER [ '%s' ] found at (%d:%d)";

  public static void updatePosition(char c) {
    if (c == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
  }

  public static int getLine() {
    return line;
  }

  public static int getColumn() {
    return column;
  }

  public static void debug(TokenType type, String value, int currentColumn) {
    String message = String.format(DEBUG_MESSAGE, type.name(), value, line, currentColumn);
    System.out.println(message);
  }

  public static void error(String value) {
    String message = String.format(ERROR_MESSAGE, value, line, column);
    System.out.println(message);
  }
}
