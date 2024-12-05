package com.ucsp.app.domain.parser.ast.node.impl;

import com.ucsp.app.domain.parser.ast.node.ASTNode;
import com.ucsp.app.domain.parser.ast.node.ASTVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProgramNode implements ASTNode {
  private final List<ASTNode> declarations;

  public ProgramNode() {
    this.declarations = new ArrayList<>();
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }

  public void addDeclaration(ASTNode declaration) {
    declarations.add(declaration);
  }
}
