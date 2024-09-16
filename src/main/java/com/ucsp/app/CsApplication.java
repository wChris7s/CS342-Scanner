package com.ucsp.app;

public class CsApplication {
    public static void main(String[] args) {
        String file = "D:/compiladores/Scanner/prueba.txt";
        readFile lector = new readFile(file);
        lector.readFile();
    }
}