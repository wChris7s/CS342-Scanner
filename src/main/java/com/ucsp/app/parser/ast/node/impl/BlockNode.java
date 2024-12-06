package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BlockNode implements ASTNode {
  private final List<ASTNode> statements;

  public BlockNode() {
    this.statements = new ArrayList<>();
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }

  public void addStatement(ASTNode statement) {
    statements.add(statement);
  }
}
