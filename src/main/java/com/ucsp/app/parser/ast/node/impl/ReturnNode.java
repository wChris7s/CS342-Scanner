package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class ReturnNode implements ASTNode {
  private final ASTNode value;

  public ReturnNode(ASTNode value) {
    this.value = value;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
