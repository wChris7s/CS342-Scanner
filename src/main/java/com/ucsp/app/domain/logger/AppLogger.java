package com.ucsp.app.domain.logger;

import com.ucsp.app.domain.logger.utils.LoggerMessage;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {

  private AppLogger() {
    throw new IllegalStateException("Utility class");
  }

  private static int line = 1;

  private static int column = 1;

  private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);

  public static void updatePosition(char c) {
    if (c == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
  }

  public static int getColumn() {
    return column;
  }

  public static void debug(TokenType type, String value, int currentColumn) {
    String message = String.format(LoggerMessage.SCANNER_INFO, type.name(), value, line, currentColumn);
    logger.info(message);
  }

  public static void error(String value) {
    String message = String.format(LoggerMessage.SCANNER_ERROR, value, line, column);
    logger.error(message);
  }

  public static void parserDebug(Token currentToken, TokenType expected) {
    String formattedMessage = String.format(LoggerMessage.PARSER_INFO,
      currentToken.tokenType().name(),
      currentToken.tokenValue(),
      expected.name());
    logger.info(formattedMessage);
  }

  public static void parserError(String message) {
    String formattedMessage = String.format(LoggerMessage.PARSER_ERROR, message);
    logger.error(formattedMessage);
  }
}
