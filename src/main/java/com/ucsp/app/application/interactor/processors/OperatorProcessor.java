package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.enums.Operator;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class OperatorProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  private final String operators;

  public OperatorProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
    this.operators = Arrays.stream(Operator.values())
      .map(Operator::toString)
      .collect(Collectors.joining());
  }

  public boolean isOperator(String token) {
    return Arrays.stream(Operator.values())
      .map(Operator::toString)
      .anyMatch(s -> s.equals(token));
  }

  @Override
  public void process() throws IOException {
    var operator = new StringBuilder();
    int currentColumn = LogPosition.getColumn();
    String lastValidOperator = null;

    while (readerManager.hasNext() && !Character.isWhitespace(readerManager.peekChar())) {
      operator.append(readerManager.peekChar());
      LogPosition.updatePosition(readerManager.getChar());
      if (isOperator(operator.toString())) {
        lastValidOperator = operator.toString();
      } else if (lastValidOperator != null) {
        log.debug(LogMessage.OPERATOR, lastValidOperator, LogPosition.getLine(), currentColumn);
        return;
      }
    }
    if (lastValidOperator != null) {
      log.debug(LogMessage.OPERATOR, lastValidOperator, LogPosition.getLine(), currentColumn);
    }
  }

  @Override
  public boolean supports(char currentChar) {
    return operators.indexOf(currentChar) != -1;
  }
}
