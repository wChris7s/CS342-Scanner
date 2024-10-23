package com.ucsp.app.domain;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.reader.TokenReader;
import com.ucsp.app.domain.token.types.TokenType;

import static com.ucsp.app.domain.token.types.impl.Category.*;
import static com.ucsp.app.domain.token.types.impl.Delimiter.*;
import static com.ucsp.app.domain.token.types.impl.Keyword.*;
import static com.ucsp.app.domain.token.types.impl.Operator.*;

public class Parser {

  private final TokenReader tokenReader;

  public Parser(TokenReader tokenReader) {
    this.tokenReader = tokenReader;
  }

  private void eat(TokenType tokenType) {
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken != null && currentToken.tokenType() == tokenType) {
      Logger.parserDebug(currentToken, tokenType);
      tokenReader.advanceToken();
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
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      Declaration();
      ProgramP();
    }
  }

  // Declaration → Function | VarDecl
  private void Declaration() {
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      Type();
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == IDENTIFIER) {
        eat(IDENTIFIER);
        if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
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
    if (isTypeToken(tokenReader.getCurrentToken())) {
      eat(tokenReader.getCurrentToken().tokenType());
    } else {
      Logger.parserError("Syntax error: expected a type but got " + tokenReader.getCurrentToken());
      throw new RuntimeException("Syntax error: expected a type but got " + tokenReader.getCurrentToken());
    }
  }

  // Params → Type Identifier Params'
  private void Params() {
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      Type();
      eat(IDENTIFIER);
      ParamsP();
    }
  }

  // Params' -> , Params | epsilon
  private void ParamsP() {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
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
    if (tokenReader.getCurrentToken() != null && isStatementToken(tokenReader.getCurrentToken())) {
      Statement();
      StmtListP();
    }
  }

  // Statement -> VarDecl | IfStmt | ForStmt | WhileStmt | ReturnStmt | ExprStmt | PrintStmt | { StmtList }
  private void Statement() {
    if (tokenReader.getCurrentToken().tokenType().equals(INT) ||
        tokenReader.getCurrentToken().tokenType().equals(BOOL) ||
        tokenReader.getCurrentToken().tokenType().equals(CHAR) ||
        tokenReader.getCurrentToken().tokenType().equals(STRING) ||
        tokenReader.getCurrentToken().tokenType().equals(VOID)) {
      VarDecl();
    } else if (tokenReader.getCurrentToken().tokenType().equals(IF)) {
      IfStmt();
    } else if (tokenReader.getCurrentToken().tokenType().equals(FOR)) {
      ForStmt();
    } else if (tokenReader.getCurrentToken().tokenType().equals(WHILE)) {
      WhileStmt();
    } else if (tokenReader.getCurrentToken().tokenType().equals(RETURN)) {
      ReturnStmt();
    } else if (tokenReader.getCurrentToken().tokenType().equals(PRINT)) {
      PrintStmt();
    } else if (tokenReader.getCurrentToken().tokenType().equals(L_BRACE)) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ELSE) {
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
    if (tokenReader.getCurrentToken().tokenType() == INT ||
      tokenReader.getCurrentToken().tokenType() == BOOL ||
      tokenReader.getCurrentToken().tokenType() == CHAR ||
      tokenReader.getCurrentToken().tokenType() == STRING) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == OR) {
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
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == AND) {
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
    if (tokenReader.getCurrentToken() != null &&
       (tokenReader.getCurrentToken().tokenType() == EQUAL || tokenReader.getCurrentToken().tokenType() == NOT_EQUAL)) {
      eat(tokenReader.getCurrentToken().tokenType());
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
    if (tokenReader.getCurrentToken() != null &&
      (tokenReader.getCurrentToken().tokenType() == LESS_THAN ||
         tokenReader.getCurrentToken().tokenType() == GREATER_THAN ||
         tokenReader.getCurrentToken().tokenType() == LESS_THAN_OR_EQUAL ||
         tokenReader.getCurrentToken().tokenType() == GREATER_THAN_OR_EQUAL)) {
      eat(tokenReader.getCurrentToken().tokenType());
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
    if (tokenReader.getCurrentToken() != null &&
      (tokenReader.getCurrentToken().tokenType() == ADDITION ||
        tokenReader.getCurrentToken().tokenType() == SUBTRACTION)) {
      eat(tokenReader.getCurrentToken().tokenType());
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
    if (tokenReader.getCurrentToken() != null &&
      (tokenReader.getCurrentToken().tokenType() == MULTIPLICATION ||
        tokenReader.getCurrentToken().tokenType() == DIVISION ||
        tokenReader.getCurrentToken().tokenType() == MODULUS)) {
      eat(tokenReader.getCurrentToken().tokenType());
      Unary();
      TermP();
    }
  }

  // Unary -> ! Unary | - Unary | Factor
  private void Unary() {
    if (tokenReader.getCurrentToken().tokenType() == LOGICAL_NOT) {
      eat(LOGICAL_NOT);
      Unary();
    } else if (tokenReader.getCurrentToken().tokenType() == SUBTRACTION) {
      eat(SUBTRACTION);
      Unary();
    } else {
      Factor();
    }
  }

  // Factor -> Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
  private void Factor() {
    if (tokenReader.getCurrentToken().tokenType().equals(IDENTIFIER)) {
      eat(IDENTIFIER);
      FactorP();
    } else if (tokenReader.getCurrentToken().tokenType().equals(INT_LITERAL)) {
      eat(INT_LITERAL);
    } else if (tokenReader.getCurrentToken().tokenType().equals(CHAR_LITERAL)) {
      eat(CHAR_LITERAL);
    } else if (tokenReader.getCurrentToken().tokenType().equals(STRING_LITERAL)) {
      eat(STRING_LITERAL);
    } else if (tokenReader.getCurrentToken().tokenType().equals(BOOL_LITERAL)) {
      eat(BOOL_LITERAL);
    } else if (tokenReader.getCurrentToken().tokenType().equals(L_PARENTHESIS)) {
      eat(L_PARENTHESIS);
      Expression();
      eat(R_PARENTHESIS);
    } else {
      Logger.parserError("Syntax error: expected a factor but got " + tokenReader.getCurrentToken());
      throw new RuntimeException("Syntax error: expected a factor but got " + tokenReader.getCurrentToken());
    }
  }

  // Factor' -> ( ExprList ) | epsilon
  private void FactorP() {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
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