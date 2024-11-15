package com.ucsp.app.domain.tree;

import java.util.ArrayList;
import java.util.List;

public class AstNode {
  private final String value;

  private final List<AstNode> children;

  public AstNode(String value) {
    this.value = value;
    this.children = new ArrayList<>();
  }

  public void addChild(AstNode child) {
    children.add(child);
  }

  public String getValue() {
    return value;
  }

  public List<AstNode> getChildren() {
    return children;
  }
}

