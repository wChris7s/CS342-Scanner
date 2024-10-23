package com.ucsp.app.domain;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.TokenType;

import java.util.List;

import static com.ucsp.app.domain.token.types.impl.Category.*;
import static com.ucsp.app.domain.token.types.impl.Delimiter.*;
import static com.ucsp.app.domain.token.types.impl.Keyword.*;
import static com.ucsp.app.domain.token.types.impl.Operator.*;

public class Parser {
  private final List<Token> tokens;

  private int currentTokenIndex;

  private Token currentToken;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.currentTokenIndex = 0;
    this.currentToken = !tokens.isEmpty() ? tokens.get(0) : null;
  }

  private void eat(TokenType tokenType) {
    Logger.parserDebug(currentToken, tokenType);
    if (currentToken != null && currentToken.tokenType() == tokenType) {
      currentTokenIndex++;
      currentToken = currentTokenIndex < tokens.size() ? tokens.get(currentTokenIndex) : null;
    } else {
      Logger.parserError("Syntax error: expected " + tokenType + " but got " + currentToken);
      throw new RuntimeException("Syntax error: expected " + tokenType + " but got " + currentToken);
    }
  }

  public void parse() {
    Program();
  }

  // Program → Declaration Program'
  private void Program() {
    Declaration();
    ProgramP();
  }

  // Program' → Declaration Program' | epsilon
  private void ProgramP() {
    if (currentToken != null && isTypeToken(currentToken)) {
      Declaration();
      ProgramP();
    }
  }

  // Declaration → Function | VarDecl
  private void Declaration() {
    if (currentToken != null && isTypeToken(currentToken)) {
      Type();
      if (currentToken != null && currentToken.tokenType() == IDENTIFIER) {
        eat(IDENTIFIER);
        if (currentToken != null && currentToken.tokenType() == L_PARENTHESIS) {
          Function();
        } else {
          VarDeclP();
        }
      }
    }
  }

  // Function -> Type Identifier ( Params ) { StmtList }
  private void Function() {
    eat(L_PARENTHESIS);
    Params();
    eat(R_PARENTHESIS);
    eat(L_BRACE);
    StmtList();
    eat(R_BRACE);
  }

  // Type -> IntType | BoolType | CharType | StringType | VoidType
  private void Type() {
    if (isTypeToken(currentToken)) {
      eat(currentToken.tokenType());
    } else {
      Logger.parserError("Syntax error: expected a type but got " + currentToken);
      throw new RuntimeException("Syntax error: expected a type but got " + currentToken);
    }
  }

  // Params → Type Identifier Params'
  private void Params() {
    if (currentToken != null && isTypeToken(currentToken)) {
      Type();
      eat(IDENTIFIER);
      ParamsP();
    }
  }

  // Params' -> , Params | epsilon
  private void ParamsP() {
    if (currentToken != null && currentToken.tokenType() == COMMA) {
      eat(COMMA);
      Params();
    }
  }

  // VarDecl -> Type Identifier VarDecl'
  private void VarDecl() {
    Type();
    eat(IDENTIFIER);
    VarDeclP();
  }

  // VarDecl' -> ; | = Expression ;
  private void VarDeclP() {
    if (currentToken != null && currentToken.tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      Expression();
    }
    eat(SEMICOLON);
  }

  // StmtList -> Statement StmtList'
  private void StmtList() {
    Statement();
    StmtListP();
  }

  // StmtList' -> Statement StmtList' | epsilon
  private void StmtListP() {
    if (currentToken != null && isStatementToken(currentToken)) {
      Statement();
      StmtListP();
    }
  }

  // Statement -> VarDecl | IfStmt | ForStmt | WhileStmt | ReturnStmt | ExprStmt | PrintStmt | { StmtList }
  private void Statement() {
    if (currentToken.tokenType().equals(INT) ||
        currentToken.tokenType().equals(BOOL) ||
        currentToken.tokenType().equals(CHAR) ||
        currentToken.tokenType().equals(STRING) ||
        currentToken.tokenType().equals(VOID)) {
      VarDecl();
    } else if (currentToken.tokenType().equals(IF)) {
      IfStmt();
    } else if (currentToken.tokenType().equals(FOR)) {
      ForStmt();
    } else if (currentToken.tokenType().equals(WHILE)) {
      WhileStmt();
    } else if (currentToken.tokenType().equals(RETURN)) {
      ReturnStmt();
    } else if (currentToken.tokenType().equals(PRINT)) {
      PrintStmt();
    } else if (currentToken.tokenType().equals(L_BRACE)) {
      eat(L_BRACE);
      StmtList();
      eat(R_BRACE);
    } else {
      ExprStmt();
    }
  }

  // IfStmt -> if ( Expression ) Statement IfStmt'
  private void IfStmt() {
    eat(IF);
    eat(L_PARENTHESIS);
    Expression();
    eat(R_PARENTHESIS);
    Statement();
    IfStmtP();
  }

  // IfStmt' -> else Statement | epsilon
  private void IfStmtP() {
    if (currentToken != null && currentToken.tokenType() == ELSE) {
      eat(ELSE);
      Statement();
    }
  }

  // ForStmt -> for ( ForInit Expression ; Expression ) Statement
  private void ForStmt() {
    eat(FOR);
    eat(L_PARENTHESIS);
    ForInit();
    Expression();
    eat(SEMICOLON);
    Expression();
    eat(R_PARENTHESIS);
    Statement();
  }

  // ForInit -> VarDecl | ExprStmt
  private void ForInit() {
    if (currentToken.tokenType() == INT ||
        currentToken.tokenType() == BOOL ||
        currentToken.tokenType() == CHAR ||
        currentToken.tokenType() == STRING) {
      VarDecl();
    }
    else {
      ExprStmt();
    }
  }

  // WhileStmt -> while ( Expression ) Statement
  private void WhileStmt() {
    eat(WHILE);
    eat(L_PARENTHESIS);
    Expression();
    eat(R_PARENTHESIS);
    Statement();
  }

  // ReturnStmt -> return Expression ; | return ;
  private void ReturnStmt() {
    eat(RETURN);
    if (currentToken != null && currentToken.tokenType() != SEMICOLON) {
      Expression();
    }
    eat(SEMICOLON);
  }

  // PrintStmt -> print ( ExprList ) ;
  private void PrintStmt() {
    eat(PRINT);
    eat(L_PARENTHESIS);
    ExprList();
    eat(R_PARENTHESIS);
    eat(SEMICOLON);
  }

  // ExprStmt -> Expression ; | ;
  private void ExprStmt() {
    if (currentToken != null && currentToken.tokenType() != SEMICOLON) {
      Expression();
    }
    eat(SEMICOLON);
  }

  // ExprList -> Expression ExprList'
  private void ExprList() {
    Expression();
    ExprListP();
  }

  // ExprList' -> , ExprList | epsilon
  private void ExprListP() {
    if (currentToken != null && currentToken.tokenType() == COMMA) {
      eat(COMMA);
      ExprList();
    }
  }

  // Expression -> OrExpr Expression'
  private void Expression() {
    OrExpr();
    ExpressionP();
  }

  // Expression' -> = Expression | epsilon
  private void ExpressionP() {
    if (currentToken != null && currentToken.tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      Expression();
    }
  }

  // OrExpr -> AndExpr OrExpr'
  private void OrExpr() {
    AndExpr();
    OrExprP();
  }

  // OrExpr' -> || AndExpr OrExpr' | epsilon
  private void OrExprP() {
    if (currentToken != null && currentToken.tokenType() == OR) {
      eat(OR);
      AndExpr();
      OrExprP();
    }
  }

  // AndExpr -> EqExpr AndExpr'
  private void AndExpr() {
    EqExpr();
    AndExprP();
  }

  // AndExpr' -> && EqExpr AndExpr' | epsilon
  private void AndExprP() {
    if (currentToken != null && currentToken.tokenType() == AND) {
      eat(AND);
      EqExpr();
      AndExprP();
    }
  }

  // EqExpr -> RelExpr EqExpr'
  private void EqExpr() {
    RelExpr();
    EqExprP();
  }

  // EqExpr' -> == RelExpr EqExpr' | != RelExpr EqExpr' | epsilon
  private void EqExprP() {
    if (currentToken != null &&
       (currentToken.tokenType() == EQUAL || currentToken.tokenType() == NOT_EQUAL)) {
      eat(currentToken.tokenType());
      RelExpr();
      EqExprP();
    }
  }

  // RelExpr -> Expr RelExpr'
  private void RelExpr() {
    Expr();
    RelExprP();
  }

  // RelExpr' -> < Expr RelExpr' | > Expr RelExpr' | <= Expr RelExpr' | >= Expr RelExpr' | epsilon
  private void RelExprP() {
    if (currentToken != null &&
      (currentToken.tokenType() == LESS_THAN ||
         currentToken.tokenType() == GREATER_THAN ||
         currentToken.tokenType() == LESS_THAN_OR_EQUAL ||
         currentToken.tokenType() == GREATER_THAN_OR_EQUAL)) {
      eat(currentToken.tokenType());
      Expr();
      RelExprP();
    }
  }

  // Expr -> Term Expr'
  private void Expr() {
    Term();
    ExprP();
  }

  // Expr' -> + Term Expr' | - Term Expr' | epsilon
  private void ExprP() {
    if (currentToken != null &&
      (currentToken.tokenType() == ADDITION ||
        currentToken.tokenType() == SUBTRACTION)) {
      eat(currentToken.tokenType());
      Term();
      ExprP();
    }
  }

  // Term -> Unary Term'
  private void Term() {
    Unary();
    TermP();
  }

  // Term' -> * Unary Term' | / Unary Term' | % Unary Term' | epsilon
  private void TermP() {
    if (currentToken != null &&
      (currentToken.tokenType() == MULTIPLICATION ||
        currentToken.tokenType() == DIVISION ||
        currentToken.tokenType() == MODULUS)) {
      eat(currentToken.tokenType());
      Unary();
      TermP();
    }
  }

  // Unary -> ! Unary | - Unary | Factor
  private void Unary() {
    if (currentToken.tokenType() == LOGICAL_NOT) {
      eat(LOGICAL_NOT);
      Unary();
    } else if (currentToken.tokenType() == SUBTRACTION) {
      eat(SUBTRACTION);
      Unary();
    } else {
      Factor();
    }
  }

  // Factor -> Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
  private void Factor() {
    if (currentToken.tokenType().equals(IDENTIFIER)) {
      eat(IDENTIFIER);
      FactorP();
    } else if (currentToken.tokenType().equals(INT_LITERAL)) {
      eat(INT_LITERAL);
    } else if (currentToken.tokenType().equals(CHAR_LITERAL)) {
      eat(CHAR_LITERAL);
    } else if (currentToken.tokenType().equals(STRING_LITERAL)) {
      eat(STRING_LITERAL);
    } else if (currentToken.tokenType().equals(BOOL_LITERAL)) {
      eat(BOOL_LITERAL);
    } else if (currentToken.tokenType().equals(L_PARENTHESIS)) {
      eat(L_PARENTHESIS);
      Expression();
      eat(R_PARENTHESIS);
    } else {
      Logger.parserError("Syntax error: expected a factor but got " + currentToken);
      throw new RuntimeException("Syntax error: expected a factor but got " + currentToken);
    }
  }

  // Factor' -> ( ExprList ) | epsilon
  private void FactorP() {
    if (currentToken != null && currentToken.tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      ExprList();
      eat(R_PARENTHESIS);
    }
  }

  private boolean isTypeToken(Token token) {
    return token.tokenType() == INT ||
           token.tokenType() == BOOL ||
           token.tokenType() == CHAR ||
           token.tokenType() == STRING ||
           token.tokenType() == VOID;
  }

  private boolean isStatementToken(Token token) {
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