package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class TypeNode implements ASTNode {
  private final String type;

  public TypeNode(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}