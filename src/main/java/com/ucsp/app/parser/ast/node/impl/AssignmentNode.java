package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class AssignmentNode implements ASTNode {
  private final IdentifierNode target;
  private final ASTNode value;

  public AssignmentNode(IdentifierNode target, ASTNode value) {
    this.target = target;
    this.value = value;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
