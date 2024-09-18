package com.ucsp.app.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReaderManager {

  private final BufferedReader reader;

  private int currentChar;

  public ReaderManager(String path) throws IOException {
    this.reader = new BufferedReader(new FileReader(path));
    this.currentChar = this.reader.read();
  }

  /**
   * Get the current character and move to the next one
   * @return Current character
   * @throws IOException
   */
  public char getChar() throws IOException {
    char character = (char) this.currentChar;
    this.currentChar = reader.read();
    return character;
  }

  /**
   * Get the current character without moving to the next one
   * @return Current character
   */
  public char peekChar() {
    return (char) this.currentChar;
  }

  /**
   * Check if there are more characters to read
   * @return True if there are more characters to read, false otherwise
   */
  public boolean hasNext() {
    return this.currentChar != -1;
  }

  /**
   * Close the reader
   * @throws IOException
   */
  public void close() throws IOException {
    reader.close();
  }
}
