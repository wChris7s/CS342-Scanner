package com.ucsp.app.domain.processors.impl;

import com.ucsp.app.domain.Scanner;
import com.ucsp.app.domain.processors.TokenProcessor;
import com.ucsp.app.domain.reader.Reader;
import com.ucsp.app.domain.token.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CharacterProcessorTest {

  @Test
  void process() throws IOException {
    String path = "src/test/resources/character.bminor";
    Reader reader = new Reader(path);
    List<TokenProcessor> processors = List.of(
      new CharacterProcessor(reader));
    Scanner scanner = new Scanner(processors, new ArrayList<>(), reader);
    scanner.tokenize();

    List<String> expected = Arrays.asList("a", "b", "c", "1");
    List<String> tokens = scanner.getTokens().stream().map(Token::tokenValue).toList();
    Assertions.assertArrayEquals(expected.toArray(), tokens.toArray());
  }

  @Test
  void supports() throws IOException {
    String path = "src/test/resources/character.bminor";
    Reader reader = new Reader(path);
    TokenProcessor processor = new CharacterProcessor(reader);
    Assertions.assertTrue(processor.supports('\''));
    Assertions.assertFalse(processor.supports('\"'));
  }
}