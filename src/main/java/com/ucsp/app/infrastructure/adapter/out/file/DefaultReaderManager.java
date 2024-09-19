package com.ucsp.app.infrastructure.adapter.out.file;

import com.ucsp.app.application.port.out.ReaderManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DefaultReaderManager implements ReaderManager {

  private final BufferedReader reader;

  private int currentChar;

  public DefaultReaderManager(String path) throws IOException {
    this.reader = new BufferedReader(new FileReader(path));
    this.currentChar = reader.read();
  }

  @Override
  public char getChar() throws IOException {
    char character = (char) currentChar;
    currentChar = reader.read();
    return character;
  }

  @Override
  public char peekChar() {
    return (char) currentChar;
  }

  @Override
  public boolean hasNext() {
    return currentChar != -1;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
