package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class ForNode implements ASTNode {
  private final ASTNode initialization;
  private final ASTNode condition;
  private final ASTNode increment;
  private final ASTNode body;

  public ForNode(ASTNode initialization, ASTNode condition, ASTNode increment, ASTNode body) {
    this.initialization = initialization;
    this.condition = condition;
    this.increment = increment;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
