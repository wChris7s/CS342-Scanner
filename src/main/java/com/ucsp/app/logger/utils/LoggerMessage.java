package com.ucsp.app.logger.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerMessage {
  public static final String SCANNER_DEBUG = "SCANNER - {} [ '{}' ] found at ({}:{})";

  public static final String SCANNER_ERROR = "SCANNER - UNRECOGNIZED CHARACTER [ '{}' ] found at ({}:{})";

  public static final String PARSER_DEBUG = "PARSER - Current token [ '{}'] with value [ '{}' ] - expected [ '{}' ]";

  public static final String PARSER_ERROR = "PARSER - {}";

  public static final String PARSER_PANIC_MODE = "PARSER - Resuming parsing at token: [ '{}' ] with value [ '{}' ]";

  public static final String PARSER_PANIC_MODE_EAT = "PARSER - Eating token: [ '{}' ] with value [ '{}' ]";

  public static final String PARSER_SYNC_INIT = "PARSER - Synchronizing parser";

  public static final String PARSER_SYNC_END = "PARSER - Parser synchronized";
}