package com.ucsp.app.domain.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {

  private final BufferedReader reader;

  private int currentChar;

  public Reader(String path) throws IOException {
    this.reader = new BufferedReader(new FileReader(path));
    this.currentChar = this.reader.read();
  }

  public char getChar() throws IOException {
    char character = (char) currentChar;
    currentChar = reader.read();
    return character;
  }

  public char peekChar() {
    return (char) currentChar;
  }

  public boolean hasNext() {
    return currentChar != -1;
  }

  public void close() throws IOException {
    reader.close();
  }
}
