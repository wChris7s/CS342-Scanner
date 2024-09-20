package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DelimiterProcessor implements TokenProcessor {

  private final ReaderManager readerManager;

  public DelimiterProcessor(ReaderManager readerManager) {
    this.readerManager = readerManager;
  }
  final int max=100;
  String[] pila= new String[max];
  int tope=-1;

  @Override
  public void process() throws IOException {
    int currentColumn = LogPosition.getColumn();
    char delimiter = readerManager.getChar();
    LogPosition.updatePosition(delimiter);

    if(delimiter=='('){
      String Pilar=Integer.toString(LogPosition.getLine())+':'+Integer.toString(currentColumn);
      pila[++tope]=Pilar;
    }
    else if(delimiter==')'){
      if (tope >= 0) {
        String valorEliminado = pila[tope--];
        while (tope >= 0) {
          valorEliminado = pila[tope--];
        }
      }
    }
    else if(delimiter==';'){
      for (int i = 0; i <= tope; i++) {
        String valorEliminado = pila[tope--];
        System.out.println(pila[i]);
      }
    }
    log.debug(LogMessage.DELIMITER, delimiter, LogPosition.getLine(), currentColumn);

  }

  @Override
  public boolean supports(char currentChar) {
    return "(){}[];".indexOf(currentChar) != -1;
  }
}
