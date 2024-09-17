package com.ucsp.app.domain;

import com.ucsp.app.infrastructure.CharacterReader;
import com.ucsp.app.application.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TokenProcessor {

  private final Validator validator;

  private final CharacterReader characterReader;

  private final Logger log = LoggerFactory.getLogger(TokenProcessor.class);

  public TokenProcessor(Validator validator, CharacterReader characterReader) {
    this.validator = validator;
    this.characterReader = characterReader;
  }

  public String getToken() throws IOException {
    StringBuilder token = new StringBuilder();
    while (characterReader.hasNext() && (validator.isLetter(characterReader.peekChar()) || validator.isDigit(characterReader.peekChar()) || characterReader.peekChar() == '_')) {
      token.append(characterReader.getChar());
    }
    return token.toString();
  }

  public void processTokens() throws IOException {
    while (characterReader.hasNext()) {
      if (validator.isLetter(characterReader.peekChar()) || characterReader.peekChar() == '_') {
        String token = getToken();
        if (validator.isKeyword(token)) {
          log.info("Reserved keyword: {}", token);
        }  else {
          log.info("Identifier: {}", token);
        }
      } else if (characterReader.peekChar() == '/') {
        char character= characterReader.getChar();
        if(characterReader.peekChar()=='/') {
          log.info("Comentario de linea");
          while(characterReader.peekChar()!='\n')
            character=characterReader.getChar();
        } else if (characterReader.peekChar()=='*') {
          log.info("Comentario de bloque");
          while(character!='*' && characterReader.getChar()!='/')
            character=characterReader.getChar();
        }

      } else {
        characterReader.getChar();
      }
    }
    characterReader.close();
  }
}
