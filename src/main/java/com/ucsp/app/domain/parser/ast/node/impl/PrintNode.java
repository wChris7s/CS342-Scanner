package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
import lombok.Getter;

import java.util.List;

@Getter
public class PrintNode implements ASTNode {
  private final List<ASTNode> expressions;

  public PrintNode(List<ASTNode> expressions) {
    this.expressions = expressions;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }

  public List<ASTNode> getExpressions() {
    return expressions;
  }
}
