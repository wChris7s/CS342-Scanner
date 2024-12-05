package com.ucsp.app.domain.parser.utilties;

import com.ucsp.app.domain.token.Token;
import lombok.experimental.UtilityClass;

import static com.ucsp.app.domain.token.types.impl.Category.IDENTIFIER;
import static com.ucsp.app.domain.token.types.impl.Delimiter.L_BRACE;
import static com.ucsp.app.domain.token.types.impl.Keyword.*;

@UtilityClass
public class TokenUtility {
  public boolean isTypeToken(Token token) {
    return token.tokenType() == INT ||
        token.tokenType() == BOOL ||
        token.tokenType() == CHAR ||
        token.tokenType() == STRING ||
        token.tokenType() == VOID;
  }

  public boolean isStatementToken(Token token) {
    return isTypeToken(token) ||
        token.tokenType() == IF ||
        token.tokenType() == FOR ||
        token.tokenType() == RETURN ||
        token.tokenType() == PRINT ||
        token.tokenType() == WHILE ||
        token.tokenType() == IDENTIFIER ||
        token.tokenType() == L_BRACE;
  }
}
