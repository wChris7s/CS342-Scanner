package com.ucsp.app.parser.ast.node.impl;

import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.ASTVisitor;
import lombok.Getter;

import java.util.List;

@Getter
public class CallNode implements ASTNode {
  private final String functionName;
  private final List<ASTNode> arguments;

  public CallNode(String functionName, List<ASTNode> arguments) {
    this.functionName = functionName;
    this.arguments = arguments;
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visit(this);
  }
}
