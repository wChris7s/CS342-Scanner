package com.ucsp.app.parser.ast.node;

import com.ucsp.app.parser.ast.node.impl.*;

public interface ASTVisitor {
  void visit(ProgramNode node);

  void visit(FunctionDeclarationNode node);

  void visit(VariableDeclarationNode node);

  void visit(BlockNode node);

  void visit(IfNode node);

  void visit(WhileNode node);

  void visit(ForNode node);

  void visit(ReturnNode node);

  void visit(PrintNode node);

  void visit(BinaryOperatorNode node);

  void visit(UnaryOperatorNode node);

  void visit(AssignmentNode node);

  void visit(IdentifierNode node);

  void visit(LiteralNode node);

  void visit(CallNode node);

  void visit(ParameterNode node);

  void visit(TypeNode node);
}
