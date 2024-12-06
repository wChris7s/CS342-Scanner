package com.ucsp.app.logger.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerMessage {
  public static final String SCANNER_DEBUG = "DEBUG SCAN - {} [ '{}' ] found at ({}:{})";

  public static final String SCANNER_ERROR = "ERROR SCAN - UNRECOGNIZED CHARACTER [ '{}' ] found at ({}:{})";

  public static final String PARSER_DEBUG = "DEBUG PARSER - Current token [ '{}'] with value [ '{}' ] - expected [ '{}' ]";

  public static final String PARSER_ERROR = "ERROR PARSER - {}";
}