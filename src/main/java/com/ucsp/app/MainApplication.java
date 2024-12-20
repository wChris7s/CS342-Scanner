package com.ucsp.app;

import com.ucsp.app.parser.Parser;
import com.ucsp.app.parser.ast.node.impl.ProgramNode;
import com.ucsp.app.parser.ast.printer.ASTPrinterGraphviz;
import com.ucsp.app.scanner.processors.TokenProcessor;
import com.ucsp.app.reader.Reader;
import com.ucsp.app.scanner.Scanner;
import com.ucsp.app.scanner.processors.impl.*;
import com.ucsp.app.token.reader.TokenReader;
import com.ucsp.app.token.reader.impl.TokenReaderImpl;

import java.io.IOException;
import java.util.List;

public class MainApplication {
  public static void main(String[] args) throws IOException {
    String path = "src/main/resources/files/main.bminor";
    TokenReader tokenReader = getTokenReader(path);
    Parser parser = new Parser(tokenReader);
    ProgramNode programNode = parser.parse();

    if (!parser.isHasErrors()) {
      ASTPrinterGraphviz printer = new ASTPrinterGraphviz();
      programNode.accept(printer);
      printer.printToFile("src/main/resources/output/ast.png");
    }
  }

  private static TokenReader getTokenReader(String path) throws IOException {
    Reader reader = new Reader(path);
    List<TokenProcessor> processors = List.of(
        new IdentifierProcessor(reader),
        new CommentProcessor(reader),
        new DelimiterProcessor(reader),
        new CharacterProcessor(reader),
        new OperatorProcessor(reader),
        new StringProcessor(reader),
        new IntegerProcessor(reader));
    Scanner scanner = new Scanner(processors, reader);
    scanner.tokenize();

    return new TokenReaderImpl(scanner.getTokens());
  }
}