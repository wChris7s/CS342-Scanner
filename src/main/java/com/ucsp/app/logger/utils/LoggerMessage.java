package com.ucsp.app.logger.utils;

import lombok.experimental.UtilityClass;
import org.fusesource.jansi.Ansi;

@UtilityClass
public class LoggerMessage {

  private static final String SCANNER_COLOR = Ansi.ansi()
      .fgBlue()
      .a("SCANNER")
      .reset()
      .toString();

  private static final String PARSER_COLOR = Ansi.ansi()
      .fgRgb(255, 210, 154) // orange
      .a("PARSER")
      .reset()
      .toString();

  public static final String SCANNER_DEBUG = SCANNER_COLOR + " - {} [ '{}' ] found at ({}:{})";

  public static final String SCANNER_ERROR = SCANNER_COLOR + " - UNRECOGNIZED CHARACTER [ '{}' ] found at ({}:{})";

  public static final String PARSER_DEBUG = PARSER_COLOR + " - Current token [ '{}'] with value [ '{}' ] - expected [ '{}' ]";

  public static final String PARSER_ERROR = PARSER_COLOR + " - {}";

  public static final String PARSER_PANIC_MODE = PARSER_COLOR + " - Resuming parsing at token: [ '{}' ] with value [ '{}' ]";

  public static final String PARSER_PANIC_MODE_EAT = PARSER_COLOR + " - Eating token: [ '{}' ] with value [ '{}' ]";

  public static final String PARSER_SYNC_INIT = PARSER_COLOR + " - Synchronizing parser";

  public static final String PARSER_SYNC_END = PARSER_COLOR + " - Parser synchronized";

  public static final String PARSER_FUNCTION_SEMANTIC_ERROR = PARSER_COLOR + " - Variable [ '{}' ] already declared in the current function scope";
}