package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
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

  public ASTNode getInitialization() {
    return initialization;
  }

  public ASTNode getCondition() {
    return condition;
  }

  public ASTNode getIncrement() {
    return increment;
  }

  public ASTNode getBody() {
    return body;
  }
}
