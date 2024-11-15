package com.ucsp.app.domain;

import com.ucsp.app.domain.logger.AppLogger;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.reader.TokenReader;
import com.ucsp.app.domain.token.types.TokenType;
import com.ucsp.app.domain.tree.AstNode;
import com.ucsp.app.domain.tree.AstVisualizer;

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
      AppLogger.parserDebug(currentToken, tokenType);
      tokenReader.advanceToken();
    } else {
      AppLogger.parserError("Syntax error: expected " + tokenType + " but got " + currentToken);
      throw new RuntimeException("Syntax error: expected " + tokenType + " but got " + currentToken);
    }
  }

  public void parse() {
    AstNode root = Program();
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != EOF) {
      AppLogger.parserError("Syntax error: expected EOF but found extra tokens");
      throw new RuntimeException("Syntax error: expected EOF but found extra tokens");
    }

    AstVisualizer visualizer = new AstVisualizer();
    visualizer.visualize(root, "ast");
  }

  // Program → Declaration Program'
  private AstNode Program() {
    AstNode node = new AstNode("Program");
    node.addChild(Declaration());
    node.addChild(ProgramP());
    return node;
  }

  // Program' → Declaration Program' | epsilon
  private AstNode ProgramP() {
    AstNode node = new AstNode("Program'");
    Token currentToken = tokenReader.getCurrentToken();

    if (currentToken != null && isTypeToken(currentToken)) {
      node.addChild(Declaration());
      node.addChild(ProgramP());
    } else if (currentToken != null && currentToken.tokenType() == EOF) {
      AppLogger.parserDebug(currentToken, EOF);
    } else {
      AppLogger.parserError("Syntax error: expected EOF but got " + currentToken);
      throw new RuntimeException("Syntax error: expected EOF but got " + currentToken);
    }
    return node;
  }


  // Declaration → Function | VarDecl
  private AstNode Declaration() {
    AstNode node = new AstNode("Declaration'");
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(Type());
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == IDENTIFIER) {
        AstNode identifierNode = new AstNode(tokenReader.getCurrentToken().tokenValue());
        eat(IDENTIFIER);
        node.addChild(identifierNode);
        if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
          node.addChild(Function());
        } else {
          node.addChild(VarDeclP());
        }
      }
    }
    return node;
  }

  // Function -> Type Identifier ( Params ) { StmtList }
  private AstNode Function() {
    AstNode node = new AstNode("Function");
    eat(L_PARENTHESIS);
    node.addChild(Params());
    eat(R_PARENTHESIS);
    eat(L_BRACE);
    node.addChild(StmtList());
    eat(R_BRACE);
    return node;
  }

  // Type -> IntType | BoolType | CharType | StringType | VoidType
  private AstNode Type() {
    if (isTypeToken(tokenReader.getCurrentToken())) {
      AstNode node = new AstNode(tokenReader.getCurrentToken().tokenValue());
      eat(tokenReader.getCurrentToken().tokenType());
      return node;
    } else {
      AppLogger.parserError("Syntax error: expected a type but got " + tokenReader.getCurrentToken());
      throw new RuntimeException("Syntax error: expected a type but got " + tokenReader.getCurrentToken());
    }
  }

  // Params → Type Identifier Params'
  private AstNode Params() {
    AstNode node = new AstNode("Params");
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(Type());
      AstNode identifierNode = new AstNode(tokenReader.getCurrentToken().tokenValue());
      eat(IDENTIFIER);
      node.addChild(identifierNode);
      node.addChild(ParamsP());
    }
    return node;
  }

  // Params' -> , Params | epsilon
  private AstNode ParamsP() {
    AstNode node = new AstNode("Params'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      node.addChild(Params());
    }
    return node;
  }

  // VarDecl -> Type Identifier VarDecl'
  private AstNode VarDecl() {
    AstNode node = new AstNode("VarDecl");
    node.addChild(Type());
    AstNode identifierNode = new AstNode(tokenReader.getCurrentToken().tokenValue());
    eat(IDENTIFIER);
    node.addChild(identifierNode);
    node.addChild(VarDeclP());
    return node;
  }

  // VarDecl' -> ; | = Expression ;
  private AstNode VarDeclP() {
    AstNode node = new AstNode("VarDecl'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      node.addChild(Expression());
    }
    eat(SEMICOLON);
    return node;
  }

  // StmtList -> Statement StmtList'
  private AstNode StmtList() {
    AstNode node = new AstNode("StmtList");
    node.addChild(Statement());
    node.addChild(StmtListP());
    return node;
  }

  // StmtList' -> Statement StmtList' | epsilon
  private AstNode StmtListP() {
    AstNode node = new AstNode("StmtList'");
    if (tokenReader.getCurrentToken() != null && isStatementToken(tokenReader.getCurrentToken())) {
      node.addChild(Statement());
      node.addChild(StmtListP());
    }
    return node;
  }

  // Statement -> VarDecl | IfStmt | ForStmt | WhileStmt | ReturnStmt | ExprStmt | PrintStmt | { StmtList }
  private AstNode Statement() {
    AstNode node = new AstNode("Statement");
    Token currentToken = tokenReader.getCurrentToken();

    if (isTypeToken(currentToken)) {
      node.addChild(VarDecl());
    } else if (currentToken.tokenType() == IF) {
      node.addChild(IfStmt());
    } else if (currentToken.tokenType() == FOR) {
      node.addChild(ForStmt());
    } else if (currentToken.tokenType() == WHILE) {
      node.addChild(WhileStmt());
    } else if (currentToken.tokenType() == RETURN) {
      node.addChild(ReturnStmt());
    } else if (currentToken.tokenType() == PRINT) {
      node.addChild(PrintStmt());
    } else if (currentToken.tokenType() == L_BRACE) {
      eat(L_BRACE);
      node.addChild(StmtList());
      eat(R_BRACE);
    } else {
      node.addChild(ExprStmt());
    }
    return node;
  }

  // IfStmt -> if ( Expression ) Statement IfStmt'
  private AstNode IfStmt() {
    AstNode node = new AstNode("IfStmt");
    eat(IF);
    eat(L_PARENTHESIS);
    node.addChild(Expression());
    eat(R_PARENTHESIS);
    node.addChild(Statement());
    node.addChild(IfStmtP());
    return node;
  }

  // IfStmt' -> else Statement | epsilon
  private AstNode IfStmtP() {
    AstNode node = new AstNode("IfStmt'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ELSE) {
      eat(ELSE);
      node.addChild(Statement());
    }
    return node;
  }

  // ForStmt -> for ( ForInit Expression ; Expression ) Statement
  private AstNode ForStmt() {
    AstNode node = new AstNode("ForStmt");
    eat(FOR);
    eat(L_PARENTHESIS);
    node.addChild(ForInit());
    node.addChild(Expression());
    eat(SEMICOLON);
    node.addChild(Expression());
    eat(R_PARENTHESIS);
    node.addChild(Statement());
    return node;
  }

  // ForInit -> VarDecl | ExprStmt
  private AstNode ForInit() {
    AstNode node = new AstNode("ForInit");
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(VarDecl());
    } else {
      node.addChild(ExprStmt());
    }
    return node;
  }

  // WhileStmt -> while ( Expression ) Statement
  private AstNode WhileStmt() {
    AstNode node = new AstNode("WhileStmt");
    eat(WHILE);
    eat(L_PARENTHESIS);
    node.addChild(Expression());
    eat(R_PARENTHESIS);
    node.addChild(Statement());
    return node;
  }

  // ReturnStmt -> return Expression ; | return ;
  private AstNode ReturnStmt() {
    AstNode node = new AstNode("ReturnStmt");
    eat(RETURN);
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
      node.addChild(Expression());
    }
    eat(SEMICOLON);
    return node;
  }

  // PrintStmt -> print ( ExprList ) ;
  private AstNode PrintStmt() {
    AstNode node = new AstNode("PrintStmt");
    eat(PRINT);
    eat(L_PARENTHESIS);
    node.addChild(ExprList());
    eat(R_PARENTHESIS);
    eat(SEMICOLON);
    return node;
  }

  // ExprStmt -> Identifier ++ ; | Identifier -- ; | Expression ; | ;
  private AstNode ExprStmt() {
    AstNode node = new AstNode("ExprStmt");
    Token currentToken = tokenReader.getCurrentToken();

    if (currentToken != null && currentToken.tokenType() == IDENTIFIER) {
      AstNode identifierNode = new AstNode(currentToken.tokenValue());
      eat(IDENTIFIER);
      node.addChild(identifierNode);
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
        node.addChild(FactorP());
        eat(SEMICOLON);
      } else if (tokenReader.getCurrentToken().tokenType() == INCREMENT) {
        eat(INCREMENT);
        eat(SEMICOLON);
      } else if (tokenReader.getCurrentToken().tokenType() == DECREMENT) {
        eat(DECREMENT);
        eat(SEMICOLON);
      } else if (tokenReader.getCurrentToken().tokenType() == SQUARE) {
        eat(SQUARE);
        eat(SEMICOLON);
      } else {
        eat(SEMICOLON);
      }
    } else if (currentToken != null && currentToken.tokenType() != SEMICOLON) {
      node.addChild(Expression());
      eat(SEMICOLON);
    } else {
      eat(SEMICOLON);
    }
    return node;
  }

  // ExprList -> Expression ExprList'
  private AstNode ExprList() {
    AstNode node = new AstNode("ExprList");
    node.addChild(Expression());
    node.addChild(ExprListP());
    return node;
  }

  // ExprList' -> , ExprList | epsilon
  private AstNode ExprListP() {
    AstNode node = new AstNode("ExprList'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      node.addChild(ExprList());
    }
    return node;
  }

  // Expression -> OrExpr Expression'
  private AstNode Expression() {
    AstNode node = new AstNode("Expression");
    node.addChild(OrExpr());
    node.addChild(ExpressionP());
    return node;
  }

  // Expression' -> = Expression | epsilon
  private AstNode ExpressionP() {
    AstNode node = new AstNode("Expression'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      node.addChild(Expression());
    }
    return node;
  }

  // OrExpr -> AndExpr OrExpr'
  private AstNode OrExpr() {
    AstNode node = new AstNode("OrExpr");
    node.addChild(AndExpr());
    node.addChild(OrExprP());
    return node;
  }

  // OrExpr' -> || AndExpr OrExpr' | epsilon
  private AstNode OrExprP() {
    AstNode node = new AstNode("OrExpr'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == OR) {
      eat(OR);
      node.addChild(AndExpr());
      node.addChild(OrExprP());
    }
    return node;
  }

  // AndExpr -> EqExpr AndExpr'
  private AstNode AndExpr() {
    AstNode node = new AstNode("AndExpr");
    node.addChild(EqExpr());
    node.addChild(AndExprP());
    return node;
  }

  // AndExpr' -> && EqExpr AndExpr' | epsilon
  private AstNode AndExprP() {
    AstNode node = new AstNode("AndExpr'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == AND) {
      eat(AND);
      node.addChild(EqExpr());
      node.addChild(AndExprP());
    }
    return node;
  }

  // EqExpr -> RelExpr EqExpr'
  private AstNode EqExpr() {
    AstNode node = new AstNode("EqExpr");
    node.addChild(RelExpr());
    node.addChild(EqExprP());
    return node;
  }

  // EqExpr' -> == RelExpr EqExpr' | != RelExpr EqExpr' | epsilon
  private AstNode EqExprP() {
    AstNode node = new AstNode("EqExpr'");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == EQUAL || tokenReader.getCurrentToken().tokenType() == NOT_EQUAL)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(RelExpr());
      node.addChild(EqExprP());
    }
    return node;
  }

  // RelExpr -> Expr RelExpr'
  private AstNode RelExpr() {
    AstNode node = new AstNode("RelExpr");
    node.addChild(Expr());
    node.addChild(RelExprP());
    return node;

  }

  // RelExpr' -> < Expr RelExpr' | > Expr RelExpr' | <= Expr RelExpr' | >= Expr RelExpr' | epsilon
  private AstNode RelExprP() {
    AstNode node = new AstNode("RelExpr'");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == LESS_THAN ||
            tokenReader.getCurrentToken().tokenType() == GREATER_THAN ||
            tokenReader.getCurrentToken().tokenType() == LESS_THAN_OR_EQUAL ||
            tokenReader.getCurrentToken().tokenType() == GREATER_THAN_OR_EQUAL)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(Expr());
      node.addChild(RelExprP());
    }
    return node;
  }

  // Expr -> Term Expr'
  private AstNode Expr() {
    AstNode node = new AstNode("Expr");
    node.addChild(Term());
    node.addChild(ExprP());
    return node;
  }

  // Expr' -> + Term Expr' | - Term Expr' | epsilon
  private AstNode ExprP() {
    AstNode node = new AstNode("Expr'");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == ADDITION ||
            tokenReader.getCurrentToken().tokenType() == SUBTRACTION)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(Term());
      node.addChild(ExprP());
    }
    return node;
  }

  // Term -> Unary Term'
  private AstNode Term() {
    AstNode node = new AstNode("Term");
    node.addChild(Unary());
    node.addChild(TermP());
    return node;
  }

  // Term' -> * Unary Term' | / Unary Term' | % Unary Term' | epsilon
  private AstNode TermP() {
    AstNode node = new AstNode("Term'");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == MULTIPLICATION ||
            tokenReader.getCurrentToken().tokenType() == DIVISION ||
            tokenReader.getCurrentToken().tokenType() == MODULUS)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(Unary());
      node.addChild(TermP());
    }
    return node;
  }

  // Unary -> ! Unary | - Unary  | Factor
  private AstNode Unary() {
    AstNode node = new AstNode("Unary");
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken.tokenType() == LOGICAL_NOT || currentToken.tokenType() == SUBTRACTION) {
      eat(currentToken.tokenType());
      node.addChild(Unary());
    } else {
      node.addChild(Factor());
    }
    return node;
  }

  // Factor -> Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
  private AstNode Factor() {
    AstNode node = new AstNode("Factor");
    Token currentToken = tokenReader.getCurrentToken();

    if (currentToken.tokenType() == IDENTIFIER) {
      AstNode identifierNode = new AstNode(currentToken.tokenValue());
      eat(IDENTIFIER);
      node.addChild(identifierNode);
      node.addChild(FactorP());
    } else if (currentToken.tokenType() == INT_LITERAL ||
        currentToken.tokenType() == CHAR_LITERAL ||
        currentToken.tokenType() == STRING_LITERAL ||
        currentToken.tokenType() == BOOL_LITERAL) {
      AstNode literalNode = new AstNode(currentToken.tokenValue());
      eat(currentToken.tokenType());
      node.addChild(literalNode);
    } else if (currentToken.tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      node.addChild(Expression());
      eat(R_PARENTHESIS);
    } else {
      AppLogger.parserError("Syntax error: expected a factor but got " + currentToken);
      throw new RuntimeException("Syntax error: expected a factor but got " + currentToken);
    }
    return node;
  }

  // Factor' -> ( ExprList ) | epsilon
  private AstNode FactorP() {
    AstNode node = new AstNode("Factor'");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != R_PARENTHESIS) {
        node.addChild(ExprList());
      }
      eat(R_PARENTHESIS);
    }
    return node;
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