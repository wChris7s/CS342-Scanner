package com.ucsp.app.application.interactor.processors;

import com.ucsp.app.application.port.out.ReaderManager;
import com.ucsp.app.domain.log.LogPosition;
import com.ucsp.app.domain.log.LogMessage;
import com.ucsp.app.domain.token.TokenProcessor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
@Slf4j
public class IntegerProcessor implements TokenProcessor {
    private final ReaderManager readerManager;

    public IntegerProcessor(ReaderManager readerManager) {
        this.readerManager = readerManager;
    }
    private boolean isValidSequence(char c) {
        return Character.isDigit(c);
    }

    @Override
    public void process() throws IOException {
        var token = new StringBuilder();
        int currentColumn = LogPosition.getColumn();
        while (readerManager.hasNext() && isValidSequence(readerManager.peekChar())) {
            token.append(readerManager.peekChar());
            LogPosition.updatePosition(readerManager.getChar());
        }
        log.debug(LogMessage.INTEGER, token, LogPosition.getLine(), currentColumn);
    }

    @Override
    public boolean supports(char currentChar) {
        return Character.isDigit(currentChar);
    }
}
