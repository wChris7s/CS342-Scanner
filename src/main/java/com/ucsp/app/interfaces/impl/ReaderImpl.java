package com.ucsp.app.interfaces.impl;

import com.ucsp.app.interfaces.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReaderImpl implements Reader {
  @Override
  public void read(String path) {
    try {
      Scanner scanner = new Scanner(new File(path));
      scanner.useDelimiter("");

      while (scanner.hasNext()) {
        String character = scanner.next();

        if (character.equals(" ")) {
          System.out.println("space");
        } else if (character.equals("\n")) {
          System.out.println("salto");
        } else {
          System.out.println(character);
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
