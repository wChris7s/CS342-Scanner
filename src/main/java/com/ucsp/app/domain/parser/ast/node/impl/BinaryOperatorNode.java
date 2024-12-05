package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class BinaryOperatorNode implements ASTNode {
  private final String operator;
  private final ASTNode left;
  private final ASTNode right;

  public BinaryOperatorNode(String operator, ASTNode left, ASTNode right) {
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
