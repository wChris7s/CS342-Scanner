package com.ucsp.app.domain.parser.ast.printer;

import com.ucsp.app.domain.parser.ast.node.*;
import com.ucsp.app.domain.parser.ast.node.impl.*;
import com.ucsp.app.domain.token.types.impl.Keyword;
import com.ucsp.app.domain.token.types.impl.Operator;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class ASTPrinterGraphviz implements ASTVisitor {
  private final MutableGraph graph;
  private final Map<ASTNode, MutableNode> nodeMap;
  private int nodeId;

  public ASTPrinterGraphviz() {
    this.graph = mutGraph("AST").setDirected(true);
    this.nodeMap = new HashMap<>();
    this.nodeId = 0;
  }

  private MutableNode createNode(String label) {
    MutableNode node = mutNode("node" + nodeId++).add(Label.of(label));
    graph.add(node);
    return node;
  }

  private void addEdge(ASTNode parent, ASTNode child) {
    if (child != null) {
      nodeMap.get(parent).addLink(nodeMap.get(child));
    }
  }

  public void printToFile(String filePath) throws IOException {
    Graphviz.fromGraph(graph).render(Format.PNG).toFile(new File(filePath));
  }

  @Override
  public void visit(ProgramNode node) {
    // MutableNode programNode = createNode("ProgramNode");
    MutableNode programNode = createNode("program");
    nodeMap.put(node, programNode);
    for (ASTNode declaration : node.getDeclarations()) {
      declaration.accept(this);
      addEdge(node, declaration);
    }
  }

  @Override
  public void visit(FunctionDeclarationNode node) {
    // MutableNode functionNode = createNode("FunctionDeclarationNode: " + node.getName());
    MutableNode functionNode = createNode("function: " + node.getName());
    nodeMap.put(node, functionNode);
    for (ParameterNode param : node.getParameters()) {
      param.accept(this);
      addEdge(node, param);
    }
    node.getBody().accept(this);
    addEdge(node, node.getBody());
  }

  @Override
  public void visit(VariableDeclarationNode node) {
    // MutableNode variableNode = createNode("VariableDeclarationNode: " + node.getName() + " of type " + node.getType());
    MutableNode variableNode = createNode("declaration");
    nodeMap.put(node, variableNode);
    if (node.getInitializer() != null) {
      node.getInitializer().accept(this);
      addEdge(node, node.getInitializer());
    }
  }

  @Override
  public void visit(BlockNode node) {
    MutableNode blockNode = createNode("block");
    nodeMap.put(node, blockNode);
    for (ASTNode statement : node.getStatements()) {
      statement.accept(this);
      addEdge(node, statement);
    }
  }

  @Override
  public void visit(IfNode node) {
    MutableNode ifNode = createNode(Keyword.IF.value());
    nodeMap.put(node, ifNode);
    node.getCondition().accept(this);
    addEdge(node, node.getCondition());
    node.getThenBranch().accept(this);
    addEdge(node, node.getThenBranch());
    if (node.getElseBranch() != null) {
      node.getElseBranch().accept(this);
      addEdge(node, node.getElseBranch());
    }
  }

  @Override
  public void visit(WhileNode node) {
    MutableNode whileNode = createNode(Keyword.WHILE.value());
    nodeMap.put(node, whileNode);
    node.getCondition().accept(this);
    addEdge(node, node.getCondition());
    node.getBody().accept(this);
    addEdge(node, node.getBody());
  }

  @Override
  public void visit(ForNode node) {
    MutableNode forNode = createNode(Keyword.FOR.value());
    nodeMap.put(node, forNode);
    node.getInitialization().accept(this);
    addEdge(node, node.getInitialization());
    node.getCondition().accept(this);
    addEdge(node, node.getCondition());
    node.getIncrement().accept(this);
    addEdge(node, node.getIncrement());
    node.getBody().accept(this);
    addEdge(node, node.getBody());
  }

  @Override
  public void visit(ReturnNode node) {
    MutableNode returnNode = createNode(Keyword.RETURN.value());
    nodeMap.put(node, returnNode);
    if (node.getValue() != null) {
      node.getValue().accept(this);
      addEdge(node, node.getValue());
    }
  }

  @Override
  public void visit(PrintNode node) {
    MutableNode printNode = createNode(Keyword.PRINT.value());
    nodeMap.put(node, printNode);
    for (ASTNode expression : node.getExpressions()) {
      expression.accept(this);
      addEdge(node, expression);
    }
  }

  @Override
  public void visit(BinaryOperatorNode node) {
    // MutableNode binaryNode = createNode("BinaryOperatorNode: " + node.getOperator());
    MutableNode binaryNode = createNode(node.getOperator());
    nodeMap.put(node, binaryNode);
    node.getLeft().accept(this);
    addEdge(node, node.getLeft());
    node.getRight().accept(this);
    addEdge(node, node.getRight());
  }

  @Override
  public void visit(UnaryOperatorNode node) {
    // MutableNode unaryNode = createNode("UnaryOperatorNode: " + node.getOperator());
    MutableNode unaryNode = createNode(node.getOperator());
    nodeMap.put(node, unaryNode);
    node.getOperand().accept(this);
    addEdge(node, node.getOperand());
  }

  @Override
  public void visit(AssignmentNode node) {
    MutableNode assignmentNode = createNode(Operator.ASSIGNMENT.value());
    nodeMap.put(node, assignmentNode);
    node.getTarget().accept(this);
    addEdge(node, node.getTarget());
    node.getValue().accept(this);
    addEdge(node, node.getValue());
  }

  @Override
  public void visit(IdentifierNode node) {
    // MutableNode identifierNode = createNode("IdentifierNode: " + node.getName());
    MutableNode identifierNode = createNode(node.getName());
    nodeMap.put(node, identifierNode);
  }

  @Override
  public void visit(LiteralNode node) {
    // MutableNode literalNode = createNode("LiteralNode: " + node.getValue() + " of type " + node.getType());
    MutableNode literalNode = createNode(node.getValue().toString());
    nodeMap.put(node, literalNode);
  }

  @Override
  public void visit(CallNode node) {
    // MutableNode callNode = createNode("CallNode: " + node.getFunctionName());
    MutableNode callNode = createNode(node.getFunctionName());
    nodeMap.put(node, callNode);
    for (ASTNode argument : node.getArguments()) {
      argument.accept(this);
      addEdge(node, argument);
    }
  }

  @Override
  public void visit(ParameterNode node) {
    // MutableNode parameterNode = createNode("ParameterNode: " + node.getName() + " of type " + node.getType());
    MutableNode parameterNode = createNode(node.getName());
    nodeMap.put(node, parameterNode);
  }

  @Override
  public void visit(TypeNode node) {
    // MutableNode typeNode = createNode("TypeNode: " + node.getType());
    MutableNode typeNode = createNode(node.getType());
    nodeMap.put(node, typeNode);
  }
}