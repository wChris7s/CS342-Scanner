package com.ucsp.app;

import com.ucsp.app.application.interactor.ScannerInteractor;
import com.ucsp.app.application.interactor.TokenProcessorInteractor;
import com.ucsp.app.domain.validators.implementations.DefaultCharacterValidator;
import com.ucsp.app.domain.validators.implementations.DefaultTokenValidator;
import com.ucsp.app.application.port.in.ScannerUseCase;
import com.ucsp.app.application.port.in.TokenProcessorUseCase;
import com.ucsp.app.domain.manager.ReaderManager;
import com.ucsp.app.domain.validators.CharacterValidator;
import com.ucsp.app.domain.validators.TokenValidator;

import java.io.IOException;

public class CsApplication {
    public static void main(String[] args) throws IOException {
        String path = "src/main/resources/files/test1.bminor";
        CharacterValidator characterValidator = new DefaultCharacterValidator();
        TokenValidator tokenValidator = new DefaultTokenValidator();
        ReaderManager readerManager = new ReaderManager(path);
        TokenProcessorUseCase tokenProcessorUseCase = new TokenProcessorInteractor(characterValidator, tokenValidator, readerManager);
        ScannerUseCase scannerUseCase = new ScannerInteractor(tokenProcessorUseCase);
        scannerUseCase.read();
    }
}