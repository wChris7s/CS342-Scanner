package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionDeclarationNode implements ASTNode {
  private final String type;
  private final String name;
  private final List<ParameterNode> parameters;
  private final BlockNode body;

  public FunctionDeclarationNode(String type, String name, List<ParameterNode> parameters, BlockNode body) {
    this.type = type;
    this.name = name;
    this.parameters = parameters;
    this.body = body;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
