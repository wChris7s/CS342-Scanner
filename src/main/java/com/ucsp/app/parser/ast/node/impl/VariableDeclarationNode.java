package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

@Getter
public class VariableDeclarationNode implements ASTNode {
  private final String type;
  private final String name;
  private final ASTNode initializer;

  public VariableDeclarationNode(String type, String name, ASTNode initializer) {
    this.type = type;
    this.name = name;
    this.initializer = initializer;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
