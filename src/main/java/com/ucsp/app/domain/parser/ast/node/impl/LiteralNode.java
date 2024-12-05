package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class LiteralNode implements ASTNode {
  private final Object value;
  private final String type;  // "int", "bool", "char", "string"

  public LiteralNode(Object value, String type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
