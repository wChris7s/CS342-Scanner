package com.ucsp.app.parser.ast.node;

public interface ASTNode {
  void accept(ASTVisitor visitor);
}
