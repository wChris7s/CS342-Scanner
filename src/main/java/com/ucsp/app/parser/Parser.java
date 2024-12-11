package com.ucsp.app.parser;

import com.ucsp.app.exceptions.ParseException;
import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.impl.*;
import com.ucsp.app.parser.utilties.TokenUtility;
import com.ucsp.app.token.Token;
import com.ucsp.app.token.reader.TokenReader;
import com.ucsp.app.token.types.TokenType;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Stack;

import static com.ucsp.app.token.types.impl.Category.*;
import static com.ucsp.app.token.types.impl.Delimiter.*;
import static com.ucsp.app.token.types.impl.Keyword.*;
import static com.ucsp.app.token.types.impl.Operator.*;

public class Parser {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(Parser.class);
  private final TokenReader tokenReader;


  @Getter
  private boolean hasErrors;

  private HashMap<String, String> globalVariables;

  private Stack<HashMap<String, String>> functionScopes;


  private Stack<HashMap<String, String>> temporaryScopes;
  public Parser(TokenReader tokenReader) {
    this.tokenReader = tokenReader;
    this.hasErrors = false;
    this.globalVariables = new HashMap<>();
    this.functionScopes = new Stack<>();
    this.temporaryScopes = new Stack<>();

  }
  private String getNodeType(ASTNode node) {
    if (node instanceof IdentifierNode) {
      return getVariableType(((IdentifierNode) node).getName());
    } else if (node instanceof LiteralNode) {
      return ((LiteralNode) node).getType();
    } else if (node instanceof BinaryOperatorNode) {

      BinaryOperatorNode binaryOp = (BinaryOperatorNode) node;
      String leftType = getNodeType(binaryOp.getLeft());
      String rightType = getNodeType(binaryOp.getRight());
      if (leftType == null || rightType == null || !leftType.equals(rightType)) {
        log.error("Type mismatch in binary operation: '{}' and '{}'.", leftType, rightType);
        return null; // Error
      }
      return leftType;
    }
    return null;
  }


  private String getVariableType(String name) {

    if (!temporaryScopes.isEmpty() && temporaryScopes.peek().containsKey(name)) {
      return temporaryScopes.peek().get(name);
    }


    if (!functionScopes.isEmpty() && functionScopes.peek().containsKey(name)) {
      return functionScopes.peek().get(name);
    }


    return globalVariables.getOrDefault(name, null);
  }

  private void validateType(String var1, String var2) {
    String type1 = getVariableType(var1);
    String type2 = getVariableType(var2);

    if (type1 == null) {
      log.error("Variable '{}' is not declared.", var1);
      return;
    }

    if (type2 == null) {
      log.error("Variable '{}' is not declared.", var2);
      return;
    }

    if (!type1.equals(type2)) {
      log.error("Type mismatch: '{}' (type: {}) and '{}' (type: {}).", var1, type1, var2, type2);
    }
  }



  private void validateArithmeticTypes(ASTNode left, ASTNode right, String operator) {
    String type1 = getNodeType(left);
    String type2 = getNodeType(right);

    if (type1 == null) {
      log.error("Left operand in '{}' operation is not declared.", operator);
      return;
    }

    if (type2 == null) {
      log.error("Right operand in '{}' operation is not declared.", operator);
      return;
    }

    if (!type1.equals(type2)) {
      log.error("Type mismatch in '{}' operation: Left operand (type: {}) and Right operand (type: {}).",
              operator, type1, type2);
    }
  }



  private void addVariable(String name, String type, boolean isTemporary) {
    if (isTemporary) {
      if (temporaryScopes.isEmpty()) {
        temporaryScopes.push(new HashMap<>());
      }
      HashMap<String, String> currentScope = temporaryScopes.peek();

      if (currentScope.containsKey(name)) {
        log.warn("Variable '{}' already declared in the current loop scope.", name);
      } else if (getVariableType(name) != null) {

        log.warn("Variable '{}' shadows an outer scope variable.", name);
        currentScope.put(name, type);
      } else {
        currentScope.put(name, type);
      }
    } else if (!functionScopes.isEmpty()) {

      HashMap<String, String> currentFunctionScope = functionScopes.peek();

      if (currentFunctionScope.containsKey(name)) {
        log.error("Variable '{}' already declared in the current function scope.", name);
      } else if (globalVariables.containsKey(name)) {

        log.warn("Variable '{}' already declared globally, but now declared in the current function scope.", name);
        currentFunctionScope.put(name, type);
      } else {
        currentFunctionScope.put(name, type);
      }
    } else {
      if (globalVariables.containsKey(name)) {
        log.error("Variable '{}' already declared globally.", name);
      } else {
        globalVariables.put(name, type);
      }
    }
  }








  private void enterFunctionScope() {
    functionScopes.push(new HashMap<>());
  }


  private void exitFunctionScope() {
    if (!functionScopes.isEmpty()) {
      functionScopes.pop();
    }
  }

  private void enterTemporaryScope() {
    temporaryScopes.push(new HashMap<>());
  }

  private void exitTemporaryScope() {
    if (!temporaryScopes.isEmpty()) {
      temporaryScopes.pop();
    }
  }


  private void panicMode() {
    log.warn(LoggerMessage.PARSER_SYNC_INIT);
    hasErrors = true;
    Token currentToken = tokenReader.getCurrentToken();
    List<TokenType> syncTokens = List.of(SEMICOLON, L_BRACE, R_BRACE, EOF);
    while (currentToken != null && !syncTokens.contains(currentToken.tokenType())) {
      log.warn(LoggerMessage.PARSER_PANIC_MODE_EAT,
              currentToken.tokenType().name(),
              currentToken.tokenValue());
      tokenReader.advanceToken();
      currentToken = tokenReader.getCurrentToken();
    }
    if (currentToken != null && currentToken.tokenType() == SEMICOLON) {
      log.warn(LoggerMessage.PARSER_PANIC_MODE,
              currentToken.tokenType().name(),
              currentToken.tokenValue());
      tokenReader.advanceToken();
    }
    log.warn(LoggerMessage.PARSER_SYNC_END);
  }

  private Token eat(TokenType tokenType) {
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken != null && currentToken.tokenType() == tokenType) {
      log.info(LoggerMessage.PARSER_DEBUG,
              currentToken.tokenType().name(),
              currentToken.tokenValue(),
              tokenType.name());  // expected

      tokenReader.advanceToken();
      return currentToken;
    } else {
      String errMessage = "Syntax error: expected " + tokenType + " but got " + currentToken;
      log.error(LoggerMessage.PARSER_ERROR, errMessage);
      panicMode();
      return tokenReader.getCurrentToken();
    }
  }

  public ProgramNode parse() {
    ProgramNode program = program();
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != EOF) {
      String errMessage = "Syntax error: expected EOF but found extra tokens";
      log.error(LoggerMessage.PARSER_ERROR, errMessage);

      throw new ParseException(errMessage);
    }
    return program;
  }

  // Program → Declaration Program'
  private ProgramNode program() {
    ProgramNode programNode = new ProgramNode();
    try {
      programNode.addDeclaration(declaration());
      programP(programNode);
    } catch (ParseException e) {
      panicMode();
    }
    return programNode;
  }

  // Program' → Declaration Program' | ε
  private void programP(ProgramNode programNode) {
    try {
      Token currentToken = tokenReader.getCurrentToken();
      if (currentToken != null && (TokenUtility.isTypeToken(currentToken) || TokenUtility.isStatementToken(currentToken))) {
        programNode.addDeclaration(declaration());
        programP(programNode);
      }
    } catch (ParseException e) {
      panicMode();
    }
  }

  // Declaration → Function | VarDecl
  private ASTNode declaration() {
    try {
      if (tokenReader.getCurrentToken() != null && TokenUtility.isTypeToken(tokenReader.getCurrentToken())) {
        String type = tokenReader.getCurrentToken().tokenValue();
        eat(tokenReader.getCurrentToken().tokenType());
        Token identifierToken = eat(IDENTIFIER);
        String name = identifierToken.tokenValue();
        if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
          return function(type, name);
        } else {
          return varDecl(type, name);
        }
      }
      throw new ParseException("Expected declaration");
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // Function -> Type Identifier ( Params ) { StmtList }
  private FunctionDeclarationNode function(String type, String name) {
    try {
      enterFunctionScope();
      eat(L_PARENTHESIS);
      List<ParameterNode> params = params();
      for (ParameterNode param : params) {
        addVariable(param.getName(), param.getType(), false);
      }
      eat(R_PARENTHESIS);
      eat(L_BRACE);
      BlockNode body = new BlockNode();
      stmtList(body);
      eat(R_BRACE);
      exitFunctionScope();
      return new FunctionDeclarationNode(type, name, params, body);
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // Params → Type Identifier Params' | ε
  private List<ParameterNode> params() {
    List<ParameterNode> parameters = new ArrayList<>();
    try {
      if (tokenReader.getCurrentToken() != null && TokenUtility.isTypeToken(tokenReader.getCurrentToken())) {
        String type = tokenReader.getCurrentToken().tokenValue();
        eat(tokenReader.getCurrentToken().tokenType());
        Token identifierToken = eat(IDENTIFIER);
        parameters.add(new ParameterNode(type, identifierToken.tokenValue()));
        paramsP(parameters);
      }
      return parameters;
    } catch (ParseException e) {
      panicMode();
      return parameters;
    }
  }

  // AssignmentOrExprStmt -> Identifier (= Expression ; | ExprStmt')
  private ASTNode assignmentOrExprStmt() {
    try {
      Token identifier = eat(IDENTIFIER);
      if (getVariableType(identifier.tokenValue()) == null) {
        log.error("Variable '{}' is not declared.", identifier.tokenValue());
      }
      if (tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
        eat(ASSIGNMENT);
        ASTNode expr = expression();
        eat(SEMICOLON);
        return new AssignmentNode(new IdentifierNode(identifier.tokenValue()), expr);
      } else {
        return completeExprStmt(new IdentifierNode(identifier.tokenValue()));
      }
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // CompleteExprStmt → ExpressionP ;
  private ASTNode completeExprStmt(ASTNode initial) {
    try {
      ASTNode expr = expressionP(initial);
      eat(SEMICOLON);
      return expr;
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // VarDecl -> Type Identifier VarDecl'
  private VariableDeclarationNode varDecl(String type, String name) {
    try {
      addVariable(name, type, false);
      ASTNode initializer = varDeclP(name);
      return new VariableDeclarationNode(type, name, initializer);
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // VarDecl' -> ; | = Expression ;
  private ASTNode varDeclP(String name) {
    try {
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
        eat(ASSIGNMENT);
        ASTNode expr = expression();
        eat(SEMICOLON);
        return new AssignmentNode(new IdentifierNode(name), expr);
      }
      eat(SEMICOLON);
      return null;
    } catch (ParseException e) {
      panicMode();
      return null;
    }
  }

  // Expression -> OrExpr Expression'
  private ASTNode expression() {
    ASTNode left = null;
    try {
      left = orExpr();
      return expressionP(left);
    } catch (ParseException e) {
      panicMode();
      return left;
    }
  }

  // ExpressionP → = Expression | ε
  private ASTNode expressionP(ASTNode left) {
    try {
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
        eat(ASSIGNMENT);
        ASTNode right = expression();
        return new AssignmentNode((IdentifierNode) left, right);
      }
      return left;
    } catch (ParseException e) {
      panicMode();
      return left;
    }
  }

  // OrExpr -> AndExpr OrExpr'
  private ASTNode orExpr() {
    ASTNode left = null;
    try {
      left = andExpr();
      return orExprP(left);
    } catch (ParseException e) {
      panicMode();
      return left;
    }
  }

  // OrExpr' -> || AndExpr OrExpr' | ε
  private ASTNode orExprP(ASTNode left) {
    try {
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == OR) {
        eat(OR);
        ASTNode right = andExpr();
        return orExprP(new BinaryOperatorNode("||", left, right));
      }
      return left;
    } catch (ParseException e) {
      panicMode();
      return left;
    }
  }

  // Factor -> Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
  private ASTNode factor() {
    Token currentToken = tokenReader.getCurrentToken();



    if (currentToken.tokenType() == IDENTIFIER) {
      Token identifier = eat(IDENTIFIER);

      if (getVariableType(identifier.tokenValue()) == null) {
        log.error("Variable '{}' is not declared.", identifier.tokenValue());
      }

      if (tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
        return factorP(new IdentifierNode(identifier.tokenValue()));
      }
      return new IdentifierNode(identifier.tokenValue());
    } else if (currentToken.tokenType() == INT_LITERAL) {
      Token literal = eat(INT_LITERAL);
      return new LiteralNode(Integer.parseInt(literal.tokenValue()), "int");
    } else if (currentToken.tokenType() == CHAR_LITERAL) {
      Token literal = eat(CHAR_LITERAL);
      return new LiteralNode(literal.tokenValue(),"char");
    } else if (currentToken.tokenType() == STRING_LITERAL) {
      Token literal = eat(STRING_LITERAL);
      return new LiteralNode(literal.tokenValue(),"string");
    } else if (currentToken.tokenType() == BOOL_LITERAL) {
      Token literal = eat(BOOL_LITERAL);
      return new LiteralNode(Boolean.parseBoolean(literal.tokenValue()), "bool");
    } else if (currentToken.tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      ASTNode expr = expression();
      eat(R_PARENTHESIS);
      return expr;
    }
    throw new ParseException("Expected factor");
  }

  // Factor' -> ( ExprList ) | ε
  private ASTNode factorP(IdentifierNode identifier) {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      List<ASTNode> arguments = new ArrayList<>();
      if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != R_PARENTHESIS) {
        arguments = exprList();
      }
      eat(R_PARENTHESIS);
      return new CallNode(identifier.getName(), arguments);
    }
    return identifier;
  }

  // Statement → VarDecl | AssignmentOrExprStmt | IfStmt | ForStmt | WhileStmt | ReturnStmt | PrintStmt | { StmtList } | ExprStmt
  private ASTNode statement() {
    Token currentToken = tokenReader.getCurrentToken();
    if (TokenUtility.isTypeToken(currentToken)) {
      String type = currentToken.tokenValue();
      eat(currentToken.tokenType());
      Token identifier = eat(IDENTIFIER);
      return varDecl(type, identifier.tokenValue());
    } else if (currentToken.tokenType() == IDENTIFIER) {
      return assignmentOrExprStmt();
    } else if (currentToken.tokenType() == IF) {
      return ifStmt();
    } else if (currentToken.tokenType() == FOR) {
      return forStmt();
    } else if (currentToken.tokenType() == WHILE) {
      return whileStmt();
    } else if (currentToken.tokenType() == RETURN) {
      return returnStmt();
    } else if (currentToken.tokenType() == PRINT) {
      return printStmt();
    } else if (currentToken.tokenType() == L_BRACE) {
      eat(L_BRACE);
      BlockNode block = new BlockNode();
      stmtList(block);
      eat(R_BRACE);
      return block;
    } else {
      return exprStmt();
    }
  }

  // IfStmt -> if ( Expression ) Statement IfStmt'
  private IfNode ifStmt() {
    eat(IF);
    eat(L_PARENTHESIS);
    ASTNode condition = expression();

    if (condition instanceof BinaryOperatorNode) {
      BinaryOperatorNode binaryOp = (BinaryOperatorNode) condition;
      if (binaryOp.getLeft() instanceof IdentifierNode && binaryOp.getRight() instanceof IdentifierNode) {
        String var1 = ((IdentifierNode) binaryOp.getLeft()).getName();
        String var2 = ((IdentifierNode) binaryOp.getRight()).getName();
        validateType(var1, var2);
      }
    }

    eat(R_PARENTHESIS);
    ASTNode thenBranch = statement();
    ASTNode elseBranch = ifStmtP();
    return new IfNode(condition, thenBranch, elseBranch);
  }

  // Type → int | bool | char | string | void
  private TypeNode type() {
    if (TokenUtility.isTypeToken(tokenReader.getCurrentToken())) {
      Token typeToken = eat(tokenReader.getCurrentToken().tokenType());
      return new TypeNode(typeToken.tokenValue());
    } else {
      String errMessage = "Syntax error: expected a type but got " + tokenReader.getCurrentToken();
      log.error(LoggerMessage.PARSER_ERROR, errMessage);

      throw new ParseException(errMessage);
    }
  }

  // ParamsP → , Type Identifier Params' | ε
  private void paramsP(List<ParameterNode> parameters) {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      parameters.addAll(params());
    }
  }

  // StmtList -> Statement StmtList'
  private void stmtList(BlockNode block) {
    block.addStatement(statement());
    stmtListP(block);
  }

  // StmtList' -> Statement StmtList' | ε
  private void stmtListP(BlockNode block) {
    if (tokenReader.getCurrentToken() != null && TokenUtility.isStatementToken(tokenReader.getCurrentToken())) {
      block.addStatement(statement());
      stmtListP(block);
    }
  }

  // IfStmt' -> else Statement | ε
  private ASTNode ifStmtP() {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ELSE) {
      eat(ELSE);
      return statement();
    }
    return null;
  }

  // ForStmt -> for ( ForInit Expression ; Expression ) Statement
  private ForNode forStmt() {
    eat(FOR);
    eat(L_PARENTHESIS);

    enterTemporaryScope();

    ASTNode initializer = forInit();
    ASTNode condition = expression();

    if (condition instanceof BinaryOperatorNode) {
      BinaryOperatorNode binaryOp = (BinaryOperatorNode) condition;
      if (binaryOp.getLeft() instanceof IdentifierNode && binaryOp.getRight() instanceof IdentifierNode) {
        String var1 = ((IdentifierNode) binaryOp.getLeft()).getName();
        String var2 = ((IdentifierNode) binaryOp.getRight()).getName();
        validateType(var1, var2);
      }
    }

    eat(SEMICOLON);
    ASTNode increment = expression();
    eat(R_PARENTHESIS);
    ASTNode body = statement();

    exitTemporaryScope();

    return new ForNode(initializer, condition, increment, body);
  }

  // ForInit -> VarDecl | ExprStmt
  private ASTNode forInit() {
    if (TokenUtility.isTypeToken(tokenReader.getCurrentToken())) {
      String type = tokenReader.getCurrentToken().tokenValue();
      eat(tokenReader.getCurrentToken().tokenType());
      Token identifierToken = eat(IDENTIFIER);
      return varDecl(type, identifierToken.tokenValue());
    } else {
      return exprStmt();
    }
  }

  // WhileStmt -> while ( Expression ) Statement
  private WhileNode whileStmt() {
    eat(WHILE);
    eat(L_PARENTHESIS);
    ASTNode condition = expression();

    if (condition instanceof BinaryOperatorNode) {
      BinaryOperatorNode binaryOp = (BinaryOperatorNode) condition;
      if (binaryOp.getLeft() instanceof IdentifierNode && binaryOp.getRight() instanceof IdentifierNode) {
        String var1 = ((IdentifierNode) binaryOp.getLeft()).getName();
        String var2 = ((IdentifierNode) binaryOp.getRight()).getName();
        validateType(var1, var2);
      }
    }

    eat(R_PARENTHESIS);
    ASTNode body = statement();
    return new WhileNode(condition, body);
  }

  // ReturnStmt -> return Expression ; | return ;
  private ReturnNode returnStmt() {
    eat(RETURN);
    ASTNode value = null;
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
      value = expression();
    }
    eat(SEMICOLON);
    return new ReturnNode(value);
  }

  // PrintStmt -> print ( ExprList ) ;
  private PrintNode printStmt() {
    eat(PRINT);
    eat(L_PARENTHESIS);
    List<ASTNode> expressions = exprList();
    eat(R_PARENTHESIS);
    eat(SEMICOLON);
    return new PrintNode(expressions);
  }

  // ExprStmt -> Expression ; | ;
  private ASTNode exprStmt() {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
      ASTNode expr = expression();
      eat(SEMICOLON);
      return expr;
    } else {
      eat(SEMICOLON);
      return null;
    }
  }

  // ExprList -> Expression ExprList'
  private List<ASTNode> exprList() {
    List<ASTNode> expressions = new ArrayList<>();
    expressions.add(expression());
    exprListP(expressions);
    return expressions;
  }

  // ExprList' -> , ExprList | epsilon
  private void exprListP(List<ASTNode> expressions) {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      expressions.addAll(exprList());
    }
  }

  // AndExpr -> EqExpr AndExpr'
  private ASTNode andExpr() {
    ASTNode left = eqExpr();
    return andExprP(left);
  }

  // AndExpr' -> && EqExpr AndExpr' | epsilon
  private ASTNode andExprP(ASTNode left) {
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == AND) {
      eat(AND);
      ASTNode right = eqExpr();
      return andExprP(new BinaryOperatorNode("&&", left, right));
    }
    return left;
  }

  // EqExpr -> RelExpr EqExpr'
  private ASTNode eqExpr() {
    ASTNode left = relExpr();
    return eqExprP(left);
  }

  // EqExpr' -> == RelExpr EqExpr' | != RelExpr EqExpr' | epsilon
  private ASTNode eqExprP(ASTNode left) {
    if (tokenReader.getCurrentToken() != null &&
            (tokenReader.getCurrentToken().tokenType() == EQUAL || tokenReader.getCurrentToken().tokenType() == NOT_EQUAL)) {
      Token operator = eat(tokenReader.getCurrentToken().tokenType());
      ASTNode right = relExpr();
      return eqExprP(new BinaryOperatorNode(operator.tokenValue(), left, right));
    }
    return left;
  }

  // RelExpr -> Expr RelExpr'
  private ASTNode relExpr() {
    ASTNode left = expr();
    return relExprP(left);
  }

  // RelExpr' -> < Expr RelExpr' | > Expr RelExpr' | <= Expr RelExpr' | >= Expr RelExpr' | epsilon
  private ASTNode relExprP(ASTNode left) {
    if (tokenReader.getCurrentToken() != null &&
            (tokenReader.getCurrentToken().tokenType() == LESS_THAN ||
                    tokenReader.getCurrentToken().tokenType() == GREATER_THAN ||
                    tokenReader.getCurrentToken().tokenType() == LESS_THAN_OR_EQUAL ||
                    tokenReader.getCurrentToken().tokenType() == GREATER_THAN_OR_EQUAL)) {
      Token operator = eat(tokenReader.getCurrentToken().tokenType());
      ASTNode right = expr();
      return relExprP(new BinaryOperatorNode(operator.tokenValue(), left, right));
    }
    return left;
  }

  // Expr -> Term Expr'
  private ASTNode expr() {
    ASTNode left = term();
    return exprP(left);
  }

  // Expr' -> + Term Expr' | - Term Expr' | epsilon
  private ASTNode exprP(ASTNode left) {
    if (tokenReader.getCurrentToken() != null &&
            (tokenReader.getCurrentToken().tokenType() == ADDITION ||
                    tokenReader.getCurrentToken().tokenType() == SUBTRACTION)) {
      Token operator = eat(tokenReader.getCurrentToken().tokenType());
      ASTNode right = term();
      validateArithmeticTypes(left, right, operator.tokenValue());
      return exprP(new BinaryOperatorNode(operator.tokenValue(), left, right));
    }
    return left;
  }

  // Term -> Unary Term'
  private ASTNode term() {
    ASTNode left = unary();
    return termP(left);
  }

  // Term' -> * Unary Term' | / Unary Term' | % Unary Term' | epsilon
  private ASTNode termP(ASTNode left) {
    if (tokenReader.getCurrentToken() != null &&
            (tokenReader.getCurrentToken().tokenType() == MULTIPLICATION ||
                    tokenReader.getCurrentToken().tokenType() == DIVISION ||
                    tokenReader.getCurrentToken().tokenType() == MODULUS)) {
      Token operator = eat(tokenReader.getCurrentToken().tokenType());
      ASTNode right = unary();
      validateArithmeticTypes(left, right, operator.tokenValue());
      return termP(new BinaryOperatorNode(operator.tokenValue(), left, right));
    }
    return left;
  }

  // Unary -> ++ Unary | -- Unary ! Unary | - Unary | Factor
  private ASTNode unary() {
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken.tokenType() == INCREMENT) {
      eat(INCREMENT);
      return new UnaryOperatorNode("++", unary());
    } else if (currentToken.tokenType() == DECREMENT) {
      eat(DECREMENT);
      return new UnaryOperatorNode("--", unary());
    } else if (currentToken.tokenType() == LOGICAL_NOT) {
      eat(LOGICAL_NOT);
      return new UnaryOperatorNode("!", unary());
    } else if (currentToken.tokenType() == SUBTRACTION) {
      eat(SUBTRACTION);
      return new UnaryOperatorNode("-", unary());
    } else {
      return factor();
    }
  }
}