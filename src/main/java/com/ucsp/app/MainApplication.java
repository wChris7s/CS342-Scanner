package com.ucsp.app;

import com.ucsp.app.domain.Parser;
import com.ucsp.app.domain.Scanner;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.processors.impl.*;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.reader.TokenReader;
import com.ucsp.app.domain.token.reader.impl.TokenReaderImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApplication {
  public static void main(String[] args) throws IOException {
    String path = "src/main/resources/files/main.bminor";
    Reader reader = new Reader(path);
    List<TokenProcessor> processors = List.of(
      new IdentifierProcessor(reader),
      new CommentProcessor(reader),
      new DelimiterProcessor(reader),
      new CharacterProcessor(reader),
      new OperatorProcessor(reader),
      new StringProcessor(reader),
      new IntegerProcessor(reader));
    List<Token> tokens = new ArrayList<>();
    Scanner scanner = new Scanner(processors, tokens, reader);
    scanner.tokenize();

    System.out.print("\n\n");

    TokenReader tokenReader = new TokenReaderImpl(tokens);
    Parser parser = new Parser(tokenReader);
    parser.parse();
  }
}