package com.ucsp.app.application.port.out;

import java.io.IOException;

public interface ReaderManager {
  /**
   * Get the current character and move to the next one
   * @return Current character
   * @throws IOException
   */
  char getChar() throws IOException;

  /**
   * Get the current character without moving to the next one
   * @return Current character
   */
  char peekChar();

  /**
   * Check if there are more characters to read
   * @return True if there are more characters to read, false otherwise
   */
  boolean hasNext();

  /**
   * Close the reader
   * @throws IOException
   */
  void close() throws IOException;
}
