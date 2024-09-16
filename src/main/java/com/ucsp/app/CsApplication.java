package com.ucsp.app;

import com.ucsp.app.interfaces.Reader;
import com.ucsp.app.interfaces.impl.ReaderImpl;

public class CsApplication {
    public static void main(String[] args) {
        String file = "src/main/resources/files/bad1.bminor";
        Reader reader = new ReaderImpl();
        reader.read(file);
    }
}