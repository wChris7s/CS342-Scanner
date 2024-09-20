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
  static int cont=0;
  @Override
  public void process() throws IOException {
    int currentColumn = LogPosition.getColumn();
    char delimiter = readerManager.getChar();
    /*if(delimiter=='('){
      cont+=1;
      System.out.println(cont+"----cont suamndo");
    }
    else if(delimiter==')'){
      cont-=1;
      System.out.println(cont+"----cont restabdo");
    }
    else if(delimiter==';'){
      System.out.println(cont+"---este es la pila");
    }*/
    LogPosition.updatePosition(delimiter);
    log.debug(LogMessage.DELIMITER, delimiter, LogPosition.getLine(), currentColumn);
  }


  @Override
  public boolean supports(char currentChar) {
    return "(){}[];".indexOf(currentChar) != -1;
  }
}
