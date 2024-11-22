package com.ucsp.app.domain.parser.ast.node;

public interface ASTNode {
  void accept(ASTVisitor visitor);
}
