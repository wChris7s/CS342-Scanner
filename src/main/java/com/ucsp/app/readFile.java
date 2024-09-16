package com.ucsp.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class readFile {
    private String file;

    public readFile(String file) {
        this.file = file;
    }

    public void readFile() {
        try {
            Scanner scanner = new Scanner(new File(file));
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
