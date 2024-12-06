package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class UnaryOperatorNode implements ASTNode {
  private final String operator;
  private final ASTNode operand;

  public UnaryOperatorNode(String operator, ASTNode operand) {
    this.operator = operator;
    this.operand = operand;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
