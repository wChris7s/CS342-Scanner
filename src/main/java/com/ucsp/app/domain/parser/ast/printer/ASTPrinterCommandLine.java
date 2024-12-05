package com.ucsp.app.domain.parser.ast.printer;

import com.ucsp.app.domain.parser.ast.node.*;
import com.ucsp.app.domain.parser.ast.node.impl.*;

public class ASTPrinterCommandLine implements ASTVisitor {
  private int indentLevel = 0;

  private void printIndent() {
    for (int i = 0; i < indentLevel; i++) {
      System.out.print("  ");
    }
  }

  @Override
  public void visit(ProgramNode node) {
    printIndent();
    System.out.println("ProgramNode");
    indentLevel++;
    for (ASTNode declaration : node.getDeclarations()) {
      declaration.accept(this);
    }
    indentLevel--;
  }

  @Override
  public void visit(FunctionDeclarationNode node) {
    printIndent();
    System.out.println("FunctionDeclarationNode: " + node.getName());
    indentLevel++;
    for (ParameterNode param : node.getParameters()) {
      param.accept(this);
    }
    node.getBody().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(VariableDeclarationNode node) {
    printIndent();
    System.out.println("VariableDeclarationNode: " + node.getName() + " of type " + node.getType());
    if (node.getInitializer() != null) {
      indentLevel++;
      node.getInitializer().accept(this);
      indentLevel--;
    }
  }

  @Override
  public void visit(BlockNode node) {
    printIndent();
    System.out.println("BlockNode");
    indentLevel++;
    for (ASTNode statement : node.getStatements()) {
      statement.accept(this);
    }
    indentLevel--;
  }

  @Override
  public void visit(IfNode node) {
    printIndent();
    System.out.println("IfNode");
    indentLevel++;
    node.getCondition().accept(this);
    node.getThenBranch().accept(this);
    if (node.getElseBranch() != null) {
      node.getElseBranch().accept(this);
    }
    indentLevel--;
  }

  @Override
  public void visit(WhileNode node) {
    printIndent();
    System.out.println("WhileNode");
    indentLevel++;
    node.getCondition().accept(this);
    node.getBody().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(ForNode node) {
    printIndent();
    System.out.println("ForNode");
    indentLevel++;
    node.getInitialization().accept(this);
    node.getCondition().accept(this);
    node.getIncrement().accept(this);
    node.getBody().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(ReturnNode node) {
    printIndent();
    System.out.println("ReturnNode");
    if (node.getValue() != null) {
      indentLevel++;
      node.getValue().accept(this);
      indentLevel--;
    }
  }

  @Override
  public void visit(PrintNode node) {
    printIndent();
    System.out.println("PrintNode");
    indentLevel++;
    for (ASTNode expression : node.getExpressions()) {
      expression.accept(this);
    }
    indentLevel--;
  }

  @Override
  public void visit(BinaryOperatorNode node) {
    printIndent();
    System.out.println("BinaryOperatorNode: " + node.getOperator());
    indentLevel++;
    node.getLeft().accept(this);
    node.getRight().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(UnaryOperatorNode node) {
    printIndent();
    System.out.println("UnaryOperatorNode: " + node.getOperator());
    indentLevel++;
    node.getOperand().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(AssignmentNode node) {
    printIndent();
    System.out.println("AssignmentNode");
    indentLevel++;
    node.getTarget().accept(this);
    node.getValue().accept(this);
    indentLevel--;
  }

  @Override
  public void visit(IdentifierNode node) {
    printIndent();
    System.out.println("IdentifierNode: " + node.getName());
  }

  @Override
  public void visit(LiteralNode node) {
    printIndent();
    System.out.println("LiteralNode: " + node.getValue() + " of type " + node.getType());
  }

  @Override
  public void visit(CallNode node) {
    printIndent();
    System.out.println("CallNode: " + node.getFunctionName());
    indentLevel++;
    for (ASTNode argument : node.getArguments()) {
      argument.accept(this);
    }
    indentLevel--;
  }

  @Override
  public void visit(ParameterNode node) {
    printIndent();
    System.out.println("ParameterNode: " + node.getName() + " of type " + node.getType());
  }

  @Override
  public void visit(TypeNode node) {
    printIndent();
    System.out.println("TypeNode: " + node.getType());
  }
}