package com.ucsp.app.infrastructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CharacterReader {
  private BufferedReader reader;

  private int currentChar;

  public CharacterReader(String path) throws IOException {
    this.reader = new BufferedReader(new FileReader(path));
    this.currentChar = this.reader.read();
  }

  public char getChar() throws IOException {
    char character = (char) this.currentChar;
    this.currentChar = reader.read();
    return character;
  }

  public char peekChar() throws IOException {
    return (char) this.currentChar;
  }

  public boolean hasNext() {
    return this.currentChar != -1;
  }

  public void close() throws IOException {
    reader.close();
  }
}