package com.ucsp.app.domain.log;

public class LogMessage {
  private static final String DEBUG = "DEBUG SCAN - ";
  private static final String INFO  = "INFO SCAN - ";
  public static final String START = INFO + "Start scanning ...";
  public static final String OPERATOR  = DEBUG + "OPERATOR [ {} ] found at ({}:{})";
  public static final String DELIMITER = DEBUG + "DELIMITER [ {} ] found at ({}:{})";
  public static final String CHAR      = DEBUG + "CHAR [ {} ] found at ({}:{})";
  public static final String STRING    = DEBUG + "STRING [ {} ] found at ({}:{})";
  public static final String INLINE_COMMENT = DEBUG + "INLINE COMMENT found at ({}:{})";
  public static final String BLOCK_COMMENT  = DEBUG + "BLOCK COMMENT found at ({}:{})";
  public static final String KEYWORD    = DEBUG + "KEYWORD [ {} ] found at ({}:{})";
  public static final String IDENTIFIER = DEBUG + "ID [ {} ] found at ({}:{})";
  public static final String INTEGER = DEBUG + "NUMBER [ {} ] found at ({}:{})";
  public static final String BLOCK_COMMENT_NF = DEBUG + "UNCLOSED BLOCK COMMENT";
}
