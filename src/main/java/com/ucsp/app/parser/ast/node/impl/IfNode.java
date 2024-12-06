package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class IfNode implements ASTNode {
  private final ASTNode condition;
  private final ASTNode thenBranch;
  private final ASTNode elseBranch;

  public IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
