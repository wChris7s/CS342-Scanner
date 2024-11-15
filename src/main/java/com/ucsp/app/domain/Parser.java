package com.ucsp.app.domain;

import com.ucsp.app.domain.logger.Logger;
import com.ucsp.app.domain.token.Token;
import com.ucsp.app.domain.token.reader.TokenReader;
import com.ucsp.app.domain.token.types.TokenType;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ucsp.app.domain.token.types.impl.Category.*;
import static com.ucsp.app.domain.token.types.impl.Delimiter.*;
import static com.ucsp.app.domain.token.types.impl.Keyword.*;
import static com.ucsp.app.domain.token.types.impl.Operator.*;

public class Parser {

  private final TokenReader tokenReader;
  private boolean isInPanicMode = false; // Variable para rastrear el modo pánico
  private final Set<TokenType> syncTokens = Set.of(SEMICOLON, R_BRACE, L_BRACE, EOF);

  public Parser(TokenReader tokenReader) {
    this.tokenReader = tokenReader;
  }

  public static class ASTNode {
    private final String type;
    private final List<ASTNode> children;

    public ASTNode(String type) {
      this.type = type;
      this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
      children.add(child);
    }

    public List<ASTNode> getChildren() {
      return children;
    }

    public String getType() {
      return type;
    }
  }

  // Método para generar la gráfica y mostrar el AST
  public void displayGraph(ASTNode root) {
    Graph graph = new SingleGraph("AST");
    graph.setStrict(false);
    graph.setAutoCreate(true);

    // Crear los nodos de la gráfica a partir del AST
    createGraphNodes(graph, root, null);

    // Configurar y mostrar la gráfica
    graph.display();
  }

  // Función recursiva para crear nodos en el grafo desde el AST
  private void createGraphNodes(Graph graph, ASTNode node, String parentId) {
    String nodeId = Integer.toHexString(System.identityHashCode(node)); // Identificador único para el nodo
    Node graphNode = graph.addNode(nodeId);
    graphNode.setAttribute("ui.label", node.getType()); // Etiqueta del nodo

    if (parentId != null) {
      graph.addEdge(parentId + "-" + nodeId, parentId, nodeId); // Conectar con el nodo padre
    }

    // Llamada recursiva para los hijos del nodo
    for (ASTNode child : node.getChildren()) {
      createGraphNodes(graph, child, nodeId);
    }
  }



  private void eat(TokenType tokenType) {
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken != null && currentToken.tokenType() == tokenType) {
      Logger.parserDebug(currentToken, tokenType);
      tokenReader.advanceToken();
      isInPanicMode = false; // Salir del modo pánico si se encuentra el token esperado
    } else {
      Logger.parserError("Syntax error: expected " + tokenType + " but got " + currentToken);
      enterPanicMode(); // Entrar en modo pánico en caso de error
      synchronize();
    }
  }

  private void synchronize() {
    while (tokenReader.getCurrentToken() != null && !syncTokens.contains(tokenReader.getCurrentToken().tokenType())) {
      tokenReader.advanceToken();
    }
    if (tokenReader.getCurrentToken() != null) {
      System.out.println(tokenReader.getCurrentToken() + " - " + "Resynchronized at " + tokenReader.getCurrentToken());
    }
  }

  public void printAST(ASTNode node) {
    printASTRecursive(node, "", true);
  }

  private void enterPanicMode() {
    isInPanicMode = true;
    Logger.parserError("Entering panic mode. Skipping tokens until a semicolon (;) is found.");


    while (tokenReader.getCurrentToken() != null) {
      Token currentToken = tokenReader.getCurrentToken();
      if (currentToken.tokenType() == L_BRACE ||
              currentToken.tokenType() == SEMICOLON ||
              currentToken.tokenType() == R_BRACE ||
              currentToken.tokenType() == EOF) {
        Logger.panicModeExit(currentToken);

        isInPanicMode = false;
        return;
      }
      tokenReader.advanceToken();
    }

    // Si se alcanza el final de los tokens sin encontrar un punto y coma
    Logger.parserError("Reached end of input while in panic mode. Parsing terminated.");
  }




  private void printASTRecursive(ASTNode node, String prefix, boolean isLast) {
    System.out.println(prefix + (isLast ? "+-- " : "+-- ") + node.getType());
    for (int i = 0; i < node.getChildren().size(); i++) {
      boolean lastChild = (i == node.getChildren().size() - 1);
      printASTRecursive(node.getChildren().get(i), prefix + (isLast ? "    " : "|   "), lastChild);
    }
  }


  public void parseAndDisplayAST() {
    ASTNode root = Program();
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != EOF) {
      Logger.parserError("Syntax error: expected EOF but found extra tokens");
      enterPanicMode();
    } else {
      printAST(root);
      displayASTGraph(root);
    }
  }

  private void displayASTGraph(ASTNode root) {
    Graph graph = new SingleGraph("AST");
    graph.setStrict(false);
    graph.setAutoCreate(true);

    addNodeToGraph(graph, root, null);

    graph.addAttribute("ui.stylesheet", "node { fill-color: black; text-color: red; text-size: 14; size: 60px, 40px; }");
    graph.addAttribute("ui.quality");
    graph.addAttribute("ui.antialias");

    Viewer viewer = graph.display();
    SpringBox layout = new SpringBox();
    viewer.enableAutoLayout(layout);
  }


  // Método auxiliar para agregar nodos y aristas al gráfico
  private void addNodeToGraph(Graph graph, ASTNode node, String parentId) {
    String nodeId = Integer.toHexString(System.identityHashCode(node)); // Generar un ID único
    Node graphNode = graph.addNode(nodeId);
    graphNode.setAttribute("ui.label", node.getType());

    if (parentId != null) {
      graph.addEdge(parentId + "-" + nodeId, parentId, nodeId, true);
    }

    for (ASTNode child : node.getChildren()) {
      addNodeToGraph(graph, child, nodeId);
    }
  }

  // Program → Declaration Program'
  private ASTNode Program() {
    ASTNode node = new ASTNode("Program");
    node.addChild(Declaration());
    node.addChild(ProgramP());
    return node;
  }

  // Program' → Declaration Program' | epsilon
  private ASTNode ProgramP() {
    ASTNode node = new ASTNode("ProgramP");
    if (tokenReader.getCurrentToken() != null && isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(Declaration());
      node.addChild(ProgramP());
    } else if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == EOF) {
        Logger.parserDebug(tokenReader.getCurrentToken(), EOF);
    } else if (!isInPanicMode) { // Solo entrar en modo pánico si no estamos en él ya
        Logger.parserError("Syntax error: expected EOF but got " + tokenReader.getCurrentToken());
        enterPanicMode();
    }
    return node;
  }

  // Declaration → Function | VarDecl
  private ASTNode Declaration() {
    ASTNode node = new ASTNode("Declaration");
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(Type());
      eat(IDENTIFIER);
      if (tokenReader.getCurrentToken().tokenType() == L_PARENTHESIS) {
        node.addChild(Function());
      } else {
        node.addChild(VarDeclP());
      }
    }
    return node;
  }

  // Function → ( Params ) { StmtList }
  private ASTNode Function() {
    ASTNode node = new ASTNode("Function");
    eat(L_PARENTHESIS);
    node.addChild(Params());
    eat(R_PARENTHESIS);
    eat(L_BRACE);
    node.addChild(StmtList());
    eat(R_BRACE);
    return node;
  }

  // Type → IntType | BoolType | CharType | StringType | VoidType
  private ASTNode Type() {
    ASTNode node = new ASTNode("Type");
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(new ASTNode(tokenReader.getCurrentToken().tokenValue()));
      eat(tokenReader.getCurrentToken().tokenType());
    } else {
      Logger.parserError("Syntax error: expected a type but got " + tokenReader.getCurrentToken());
      enterPanicMode();
    }
    return node;
  }

  // Params → Type Identifier Params'
  private ASTNode Params() {
    ASTNode node = new ASTNode("Params");
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(Type());
      eat(IDENTIFIER);
      node.addChild(ParamsP());
    }
    return node;
  }

  // Params' → , Params | epsilon
  private ASTNode ParamsP() {
    ASTNode node = new ASTNode("ParamsP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      node.addChild(Params());
    }
    return node;
  }

  // VarDecl → Type Identifier VarDecl'
  private ASTNode VarDecl() {
    ASTNode node = new ASTNode("VarDecl");
    node.addChild(Type());
    eat(IDENTIFIER);
    node.addChild(VarDeclP());
    return node;
  }

  // VarDecl' → ; | = Expression ;
  private ASTNode VarDeclP() {
    ASTNode node = new ASTNode("VarDeclP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      node.addChild(Expression());
    }
    eat(SEMICOLON);
    return node;
  }

  // StmtList → Statement StmtList'
  private ASTNode StmtList() {
    ASTNode node = new ASTNode("StmtList");
    node.addChild(Statement());
    node.addChild(StmtListP());
    return node;
  }

  // StmtList' → Statement StmtList' | epsilon
  private ASTNode StmtListP() {
    ASTNode node = new ASTNode("StmtListP");
    if (isStatementToken(tokenReader.getCurrentToken())) {
      node.addChild(Statement());
      node.addChild(StmtListP());
    }
    return node;
  }

  // Statement → VarDecl | IfStmt | ForStmt | WhileStmt | ReturnStmt | ExprStmt | PrintStmt | { StmtList }
  private ASTNode Statement() {
    ASTNode node = new ASTNode("Statement");
    TokenType tokenType = tokenReader.getCurrentToken().tokenType();
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(VarDecl());
    } else if (tokenType == IF) {
      node.addChild(IfStmt());
    } else if (tokenType == FOR) {
      node.addChild(ForStmt());
    } else if (tokenType == WHILE) {
      node.addChild(WhileStmt());
    } else if (tokenType == RETURN) {
      node.addChild(ReturnStmt());
    } else if (tokenType == PRINT) {
      node.addChild(PrintStmt());
    } else if (tokenType == L_BRACE) {
      eat(L_BRACE);
      node.addChild(StmtList());
      eat(R_BRACE);
    } else {
      node.addChild(ExprStmt());
    }
    return node;
  }

  // IfStmt → if ( Expression ) Statement IfStmt'
  private ASTNode IfStmt() {
    ASTNode node = new ASTNode("IfStmt");
    eat(IF);
    eat(L_PARENTHESIS);
    node.addChild(Expression());
    eat(R_PARENTHESIS);
    node.addChild(Statement());
    node.addChild(IfStmtP());
    return node;
  }

  // IfStmt' → else Statement | epsilon
  private ASTNode IfStmtP() {
    ASTNode node = new ASTNode("IfStmtP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ELSE) {
      eat(ELSE);
      node.addChild(Statement());
    }
    return node;
  }

  // ForStmt → for ( ForInit Expression ; Expression ) Statement
  private ASTNode ForStmt() {
    ASTNode node = new ASTNode("ForStmt");
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

  // ForInit → VarDecl | ExprStmt
  private ASTNode ForInit() {
    ASTNode node = new ASTNode("ForInit");
    if (isTypeToken(tokenReader.getCurrentToken())) {
      node.addChild(VarDecl());
    } else {
      node.addChild(ExprStmt());
    }
    return node;
  }

  // WhileStmt → while ( Expression ) Statement
  private ASTNode WhileStmt() {
    ASTNode node = new ASTNode("WhileStmt");
    eat(WHILE);
    eat(L_PARENTHESIS);
    node.addChild(Expression());
    eat(R_PARENTHESIS);
    node.addChild(Statement());
    return node;
  }

  // ReturnStmt → return Expression ; | return ;
  private ASTNode ReturnStmt() {
    ASTNode node = new ASTNode("ReturnStmt");
    eat(RETURN);
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
      node.addChild(Expression());
    }
    eat(SEMICOLON);
    return node;
  }

  // PrintStmt → print ( ExprList ) ;
  private ASTNode PrintStmt() {
    ASTNode node = new ASTNode("PrintStmt");
    eat(PRINT);
    eat(L_PARENTHESIS);
    node.addChild(ExprList());
    eat(R_PARENTHESIS);
    eat(SEMICOLON);
    return node;
  }

  // ExprStmt → Expression ; | ;
  private ASTNode ExprStmt() {
    ASTNode node = new ASTNode("ExprStmt");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() != SEMICOLON) {
      node.addChild(Expression());
    }
    eat(SEMICOLON);
    return node;
  }

  // ExprList → Expression ExprList'
  private ASTNode ExprList() {
    ASTNode node = new ASTNode("ExprList");
    node.addChild(Expression());
    node.addChild(ExprListP());
    return node;
  }

  // ExprList' → , Expression ExprList' | epsilon
  private ASTNode ExprListP() {
    ASTNode node = new ASTNode("ExprListP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == COMMA) {
      eat(COMMA);
      node.addChild(Expression());
      node.addChild(ExprListP());
    }
    return node;
  }

  // Expression → OrExpr Expression'
  private ASTNode Expression() {
    ASTNode node = new ASTNode("Expression");
    node.addChild(OrExpr());
    node.addChild(ExpressionP());
    return node;
  }

  // Expression' → = Expression | epsilon
  private ASTNode ExpressionP() {
    ASTNode node = new ASTNode("ExpressionP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == ASSIGNMENT) {
      eat(ASSIGNMENT);
      node.addChild(Expression());
    }
    return node;
  }

  // OrExpr → AndExpr OrExpr'
  private ASTNode OrExpr() {
    ASTNode node = new ASTNode("OrExpr");
    node.addChild(AndExpr());
    node.addChild(OrExprP());
    return node;
  }

    // OrExpr' → || AndExpr OrExpr' | epsilon
    private ASTNode OrExprP() {
        ASTNode node = new ASTNode("OrExprP");
        if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == OR) {
            eat(OR);
            node.addChild(AndExpr());
            node.addChild(OrExprP());
        }
        return node;
    }

  // AndExpr → EqExpr AndExpr'
  private ASTNode AndExpr() {
    ASTNode node = new ASTNode("AndExpr");
    node.addChild(EqExpr());
    node.addChild(AndExprP());
    return node;
  }

  // AndExpr' → && EqExpr AndExpr' | epsilon
  private ASTNode AndExprP() {
    ASTNode node = new ASTNode("AndExprP");
    if (tokenReader.getCurrentToken() != null && tokenReader.getCurrentToken().tokenType() == AND) {
      eat(AND);
      node.addChild(EqExpr());
      node.addChild(AndExprP());
    }
    return node;
  }

  // EqExpr → RelExpr EqExpr'
  private ASTNode EqExpr() {
    ASTNode node = new ASTNode("EqExpr");
    node.addChild(RelExpr());
    node.addChild(EqExprP());
    return node;
  }

  // EqExpr' → == RelExpr EqExpr' | != RelExpr EqExpr' | epsilon
  private ASTNode EqExprP() {
    ASTNode node = new ASTNode("EqExprP");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == EQUAL || tokenReader.getCurrentToken().tokenType() == NOT_EQUAL)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(RelExpr());
      node.addChild(EqExprP());
    }
    return node;
  }

  // RelExpr → Expr RelExpr'
  private ASTNode RelExpr() {
    ASTNode node = new ASTNode("RelExpr");
    node.addChild(Expr());
    node.addChild(RelExprP());
    return node;
  }

  // RelExpr' → < Expr RelExpr' | > Expr RelExpr' | <= Expr RelExpr' | >= Expr RelExpr' | epsilon
  private ASTNode RelExprP() {
    ASTNode node = new ASTNode("RelExprP");
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

  // Expr → Term Expr'
  private ASTNode Expr() {
    ASTNode node = new ASTNode("Expr");
    node.addChild(Term());
    node.addChild(ExprP());
    return node;
  }

  // Expr' → + Term Expr' | - Term Expr' | epsilon
  private ASTNode ExprP() {
    ASTNode node = new ASTNode("ExprP");
    if (tokenReader.getCurrentToken() != null &&
        (tokenReader.getCurrentToken().tokenType() == ADDITION || tokenReader.getCurrentToken().tokenType() == SUBTRACTION)) {
      eat(tokenReader.getCurrentToken().tokenType());
      node.addChild(Term());
      node.addChild(ExprP());
    }
    return node;
  }

  // Term → Unary Term'
  private ASTNode Term() {
    ASTNode node = new ASTNode("Term");
    node.addChild(Unary());
    node.addChild(TermP());
    return node;
  }

  // Term' → * Unary Term' | / Unary Term' | % Unary Term' | epsilon
  private ASTNode TermP() {
    ASTNode node = new ASTNode("TermP");
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

  // Unary → ! Unary | - Unary  | Factor
  private ASTNode Unary() {
    ASTNode node = new ASTNode("Unary");
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken.tokenType() == LOGICAL_NOT) {
      eat(LOGICAL_NOT);
      node.addChild(Unary());
    } else if (currentToken.tokenType() == SUBTRACTION) {
      eat(SUBTRACTION);
      node.addChild(Unary());
    } else {
      node.addChild(Factor());
    }
    return node;
  }

  // Factor → Identifier Factor' | IntLiteral | CharLiteral | StringLiteral | BoolLiteral | ( Expression )
  private ASTNode Factor() {
    ASTNode node = new ASTNode("Factor");
    Token currentToken = tokenReader.getCurrentToken();
    if (currentToken.tokenType() == IDENTIFIER) {
      eat(IDENTIFIER);
      node.addChild(FactorP());
    } else if (currentToken.tokenType() == INT_LITERAL) {
      eat(INT_LITERAL);
    } else if (currentToken.tokenType() == CHAR_LITERAL) {
      eat(CHAR_LITERAL);
    } else if (currentToken.tokenType() == STRING_LITERAL) {
      eat(STRING_LITERAL);
    } else if (currentToken.tokenType() == BOOL_LITERAL) {
      eat(BOOL_LITERAL);
    } else if (currentToken.tokenType() == L_PARENTHESIS) {
      eat(L_PARENTHESIS);
      node.addChild(Expression());
      eat(R_PARENTHESIS);
    } else {
      Logger.parserError("Syntax error: expected a factor but got " + tokenReader.getCurrentToken());
      enterPanicMode();
    }
    return node;
  }

  // Factor' → ( ExprList ) | epsilon
  private ASTNode FactorP() {
    ASTNode node = new ASTNode("FactorP");
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