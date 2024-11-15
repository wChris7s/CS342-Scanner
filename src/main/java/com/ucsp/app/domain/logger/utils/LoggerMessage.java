package com.ucsp.app.domain.logger.utils;

public class LoggerMessage {
  public static final String SCANNER_INFO = "DEBUG SCAN - %s [ '%s' ] found at (%d:%d)";

  public static final String SCANNER_ERROR = "ERROR SCAN - UNRECOGNIZED CHARACTER [ '%s' ] found at (%d:%d)";

  public static final String PARSER_INFO = "DEBUG PARSER - Current token [ '%s'] with value [ '%s' ], expected [ '%s' ]";

  public static final String PARSER_ERROR = "ERROR PARSER - %s";


  private LoggerMessage() {
    throw new IllegalStateException("Utility class");
  }
}