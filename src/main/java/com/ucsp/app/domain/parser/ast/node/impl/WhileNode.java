package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class WhileNode implements ASTNode {
  private final ASTNode condition;
  private final ASTNode body;

  public WhileNode(ASTNode condition, ASTNode body) {
    this.condition = condition;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }

  public ASTNode getCondition() {
    return condition;
  }

  public ASTNode getBody() {
    return body;
  }
}
