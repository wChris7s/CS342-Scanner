package com.ucsp.app.domain;

import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.types.TokenType;
import com.ucsp.app.domain.token.types.impl.Delimiter;
import com.ucsp.app.domain.token.types.impl.Keyword;
import com.ucsp.app.domain.token.types.impl.Operator;

import java.util.List;

import static com.ucsp.app.domain.token.types.impl.Category.*;
import static com.ucsp.app.domain.token.types.impl.Delimiter.L_BRACE;
import static com.ucsp.app.domain.token.types.impl.Delimiter.L_PARENTHESIS;
import static com.ucsp.app.domain.token.types.impl.Keyword.*;

public class Parser {
  private List<Token> tokens;
  private int currentTokenIndex;
  private Token currentToken;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.currentTokenIndex = 0;
    this.currentToken = tokens.size() > 0 ? tokens.get(0) : null;
  }


  private void log(String message) {
    System.out.println(message);
  }

  private void eat(TokenType tokenType) {
    log("Current token: " + currentToken + ", expected: " + tokenType);
    if (currentToken != null && currentToken.tokenType() == tokenType) {
      currentTokenIndex++;
      if (currentTokenIndex < tokens.size()) {
        currentToken = tokens.get(currentTokenIndex);
      } else {
        currentToken = null;
      }
    } else {
      throw new RuntimeException("Error de sintaxis: se esperaba " + tokenType + " pero se obtuvo " + currentToken);
    }
  }

  public void parse() {
    // Comenzamos con la regla de la gramática inicial: Program
    Program();
  }

  private void Program() {
    // Program -> Declaration Program'
    Declaration();
    ProgramP();
  }

  private void ProgramP() {
    // Program' -> Declaration Program' | epsilon
    if (currentToken != null && isTypeToken(currentToken)) {
      Declaration();
      ProgramP();
    }
    // Si no hay más declaraciones, épsilon es válido
  }

  private void Declaration() {
    // Declaration -> Function | VarDecl
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

  private void Function() {
    // Function -> Type Identifier ( Params ) { StmtList }
    eat(L_PARENTHESIS);
    Params();
    eat(Delimiter.R_PARENTHESIS);
    eat(L_BRACE);
    StmtList();
    eat(Delimiter.R_BRACE);
  }

  private void Type() {
    // Type -> IntType | BoolType | CharType | StringType | VoidType
    if (isTypeToken(currentToken)) {
      eat(currentToken.tokenType());
    } else {
      throw new RuntimeException("Error de sintaxis: se esperaba un tipo pero se obtuvo " + currentToken);
    }
  }

  private void Params() {
    // Params -> Type Identifier Params'
    if (currentToken != null && isTypeToken(currentToken)) {
      Type();
      eat(IDENTIFIER);
      ParamsP();
    }
    // Si no hay parámetros, épsilon es válido
  }

  private void ParamsP() {
    // Params' -> , Params | epsilon
    if (currentToken != null && currentToken.tokenType() == Delimiter.COMMA) {
      eat(Delimiter.COMMA);
      Params();
    }
    // Si no hay más parámetros, épsilon es válido
  }

  private void VarDecl() {
    // VarDecl -> Type Identifier VarDecl'
    Type();
    eat(IDENTIFIER);
    VarDeclP();
  }

  private void VarDeclP() {
    // VarDecl' -> ; | = Expression ;
    if (currentToken != null && currentToken.tokenType() == Operator.ASSIGNMENT) {
      eat(Operator.ASSIGNMENT);
      Expression();
    }
    eat(Delimiter.SEMICOLON);
  }

  private void StmtList() {
    // StmtList -> Statement StmtList'
    Statement();
    StmtListP();
  }

  private void StmtListP() {
    // StmtList' -> Statement StmtList' | epsilon
    if (currentToken != null && isStatementToken(currentToken)) {
      Statement();
      StmtListP();
    }
    // Si no hay más declaraciones, épsilon es válido
  }

  private void Statement() {
    // Statement -> VarDecl | IfStmt | ForStmt | ReturnStmt | ExprStmt | PrintStmt | { StmtList }
    if (currentToken.tokenType().equals(INT) || currentToken.tokenType().equals(BOOL) || currentToken.tokenType().equals(CHAR) || currentToken.tokenType().equals(STRING) || currentToken.tokenType().equals(VOID)) {
      VarDecl();
    } else if (currentToken.tokenType().equals(IF)) {
      IfStmt();
    } else if (currentToken.tokenType().equals(FOR)) {
      ForStmt();
    } else if (currentToken.tokenType().equals(WHILE)) {
      WhileStmt();
    }else if (currentToken.tokenType().equals(RETURN)) {
      ReturnStmt();
    } else if (currentToken.tokenType().equals(PRINT)) {
      PrintStmt();
    } else if (currentToken.tokenType().equals(L_BRACE)) {
      eat(L_BRACE);
      StmtList();
      eat(Delimiter.R_BRACE);
    } else {
      ExprStmt();
    }
  }

  private void IfStmt() {
    // IfStmt -> if ( Expression ) Statement IfStmt'
    eat(Keyword.IF);
    eat(L_PARENTHESIS);
    Expression();
    eat(Delimiter.R_PARENTHESIS);
    Statement();
    IfStmtP();
  }

  private void IfStmtP() {
    // IfStmt' -> else Statement | epsilon
    if (currentToken != null && currentToken.tokenType() == Keyword.ELSE) {
      eat(Keyword.ELSE);
      Statement();
    }
    // Si no hay else, épsilon es válido
  }

  private void ForStmt() {
    // ForStmt -> for ( ExprStmt Expression ; ExprStmt ) Statement
    eat(FOR);
    eat(L_PARENTHESIS);
    ExprStmt();
    Expression();
    eat(Delimiter.SEMICOLON);
    ExprStmt();
    eat(Delimiter.R_PARENTHESIS);
    Statement();
  }

  private void WhileStmt(){
    // WhileStmt -> while ( Expression ) Statement
    eat(WHILE);
    eat(L_PARENTHESIS);
    Expression();
    eat(Delimiter.R_PARENTHESIS);
    Statement();
  }

  private void ReturnStmt() {
    // ReturnStmt -> return Expression ;
    eat(RETURN);
    Expression();
    eat(Delimiter.SEMICOLON);
  }

  private void PrintStmt() {
    // PrintStmt -> print ( ExprList ) ;
    eat(PRINT);
    eat(L_PARENTHESIS);
    ExprList();
    eat(Delimiter.R_PARENTHESIS);
    eat(Delimiter.SEMICOLON);
  }

  private void ExprStmt() {
    // ExprStmt -> Expression ; | ;
    if (currentToken != null && currentToken.tokenType() != Delimiter.SEMICOLON) {
      Expression();
    }
    eat(Delimiter.SEMICOLON);
  }

  private void ExprList() {
    // ExprList -> Expression ExprList'
    Expression();
    ExprListP();
  }

  private void ExprListP() {
    // ExprList' -> , ExprList | epsilon
    if (currentToken != null && currentToken.tokenType() == Delimiter.COMMA) {
      eat(Delimiter.COMMA);
      ExprList();
    }
    // Si no hay más expresiones, épsilon es válido
  }

  private void Expression() {
    // Expression -> Identifier Expression' | OrExpr
    if (currentToken.tokenType() == IDENTIFIER) {
      eat(IDENTIFIER);
      ExpressionP();
    } else {
      OrExpr();
    }
  }

  private void ExpressionP() {
    // Expression' -> = Expression | epsilon
    if (currentToken != null && currentToken.tokenType() == Operator.ASSIGNMENT) {
      eat(Operator.ASSIGNMENT);
      Expression();
    }
    // Si no hay asignación, épsilon es válido
  }

  private void OrExpr() {
    // OrExpr -> AndExpr OrExpr'
    AndExpr();
    OrExprP();
  }

  private void OrExprP() {
    // OrExpr' -> || AndExpr OrExpr' | epsilon
    if (currentToken != null && currentToken.tokenType() == Operator.OR) {
      eat(Operator.OR);
      AndExpr();
      OrExprP();
    }
    // Si no hay más OR, épsilon es válido
  }

  private void AndExpr() {
    // AndExpr -> EqExpr AndExpr'
    EqExpr();
    AndExprP();
  }

  private void AndExprP() {
    // AndExpr' -> && EqExpr AndExpr' | epsilon
    if (currentToken != null && currentToken.tokenType() == Operator.AND) {
      eat(Operator.AND);
      EqExpr();
      AndExprP();
    }
    // Si no hay más AND, épsilon es válido
  }

  private void EqExpr() {
    // EqExpr -> RelExpr EqExpr'
    RelExpr();
    EqExprP();
  }

  private void EqExprP() {
    // EqExpr' -> == RelExpr EqExpr' | != RelExpr EqExpr' | epsilon
    if (currentToken != null && (currentToken.tokenType() == Operator.EQUAL || currentToken.tokenType() == Operator.NOT_EQUAL)) {
      eat(currentToken.tokenType());
      RelExpr();
      EqExprP();
    }
    // Si no hay más comparaciones, épsilon es válido
  }

  private void RelExpr() {
    // RelExpr -> Expr RelExpr'
    Expr();
    RelExprP();
  }

  private void RelExprP() {
    // RelExpr' -> < Expr RelExpr' | > Expr RelExpr' | <= Expr RelExpr' | >= Expr RelExpr' | epsilon
    if (currentToken != null && (currentToken.tokenType() == Operator.LESS_THAN || currentToken.tokenType() == Operator.GREATER_THAN ||
      currentToken.tokenType() == Operator.LESS_THAN_OR_EQUAL || currentToken.tokenType() == Operator.GREATER_THAN_OR_EQUAL)) {
      eat(currentToken.tokenType());
      Expr();
      RelExprP();
    }
    // Si no hay más relaciones, épsilon es válido
  }

  private void Expr() {
    // Expr -> Term Expr'
    Term();
    ExprP();
  }

  private void ExprP() {
    // Expr' -> + Term Expr' | - Term Expr' | epsilon
    if (currentToken != null && (currentToken.tokenType() == Operator.ADDITION || currentToken.tokenType() == Operator.SUBTRACTION)) {
      eat(currentToken.tokenType());
      Term();
      ExprP();
    }
    // Si no hay más términos, épsilon es válido
  }

  private void Term() {
    // Term -> Unary Term'
    Unary();
    TermP();
  }

  private void TermP() {
    // Term' -> * Unary Term' | / Unary Term' | % Unary Term' | epsilon
    if (currentToken != null && (currentToken.tokenType() == Operator.MULTIPLICATION || currentToken.tokenType() == Operator.DIVISION ||
      currentToken.tokenType() == Operator.MODULUS)) {
      eat(currentToken.tokenType());
      Unary();
      TermP();
    }
    // Si no hay más factores, épsilon es válido
  }

  private void Unary() {
    // Unary -> ! Unary | - Unary | Factor
    if (currentToken.tokenType() == Operator.LOGICAL_NOT) {
      eat(Operator.LOGICAL_NOT);
      Unary();
    } else if (currentToken.tokenType() == Operator.SUBTRACTION) {
      eat(Operator.SUBTRACTION);
      Unary();
    } else {
      Factor();
    }
  }

  private void Factor() {
    // Factor -> Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
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
      eat(Delimiter.R_PARENTHESIS);
    } else {
      throw new RuntimeException("Error de sintaxis: se esperaba un factor pero se obtuvo " + currentToken);
    }
  }

  private void FactorP() {
    // Factor' -> ( ExprList ) | epsilon
    if (currentToken != null && currentToken.tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      ExprList();
      eat(Delimiter.R_PARENTHESIS);
    }
    // Si no hay llamada a función, épsilon es válido
  }

  private boolean isTypeToken(Token token) {
    return token.tokenType() == Keyword.INT || token.tokenType() == Keyword.BOOL ||
      token.tokenType() == Keyword.CHAR || token.tokenType() == Keyword.STRING ||
      token.tokenType() == Keyword.VOID;
  }

  private boolean isStatementToken(Token token) {
    return isTypeToken(token) || token.tokenType() == Keyword.IF || token.tokenType() == FOR ||
      token.tokenType() == RETURN || token.tokenType() == PRINT ||
      token.tokenType() == IDENTIFIER || token.tokenType() == L_BRACE;
  }
}