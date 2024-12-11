package com.ucsp.app.semantic;

import com.ucsp.app.logger.utils.LoggerMessage;
import com.ucsp.app.parser.ast.node.ASTNode;
import com.ucsp.app.parser.ast.node.impl.BinaryOperatorNode;
import com.ucsp.app.parser.ast.node.impl.IdentifierNode;
import com.ucsp.app.parser.ast.node.impl.LiteralNode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Stack;

@Slf4j
public class SemanticManager {

  private final HashMap<String, String> globalVariables;
  private final Stack<HashMap<String, String>> functionScopes;
  private final Stack<HashMap<String, String>> blockScopes;

  public SemanticManager() {
    this.globalVariables = new HashMap<>();
    this.functionScopes = new Stack<>();
    this.blockScopes = new Stack<>();
  }

  public String getVariableType(String name) {
    if (!blockScopes.isEmpty()) {
      for (int i = blockScopes.size() - 1; i >= 0; i--) {
        HashMap<String, String> scope = blockScopes.get(i);
        if (scope.containsKey(name)) {
          return scope.get(name);
        }
      }
    }
    if (!functionScopes.isEmpty() && functionScopes.peek().containsKey(name)) {
      return functionScopes.peek().get(name);
    }
    return globalVariables.getOrDefault(name, null);
  }

  public void validateType(String var1, String var2) {
    String type1 = getVariableType(var1);
    String type2 = getVariableType(var2);
    if (type1 == null) {
      log.error("Variable '{}' is not declared.", var1);
      return;
    }
    if (type2 == null) {
      log.error("Variable '{}' is not declared.", var2);
      return;
    }
    if (!type1.equals(type2)) {
      log.error("Type mismatch: '{}' (type: {}) and '{}' (type: {}).", var1, type1, var2, type2);
    }
  }

  public void validateArithmeticTypes(ASTNode left, ASTNode right, String operator) {
    String type1 = getNodeType(left);
    String type2 = getNodeType(right);

    if (type1 == null) {
      log.error("Left operand in '{}' operation is not declared.", operator);
      return;
    }
    if (type2 == null) {
      log.error("Right operand in '{}' operation is not declared.", operator);
      return;
    }
    if (!type1.equals(type2)) {
      log.error("Type mismatch in '{}' operation: Left operand (type: {}) and Right operand (type: {}).",
          operator, type1, type2);
    }
  }

  public void addVariable(String name, String type, boolean isBlockScope) {
    if (isBlockScope) {
      if (blockScopes.isEmpty()) {
        blockScopes.push(new HashMap<>());
      }
      HashMap<String, String> currentScope = blockScopes.peek();
      if (currentScope.containsKey(name)) {
        log.warn("Variable '{}' already declared in the current block scope.", name);
      } else {
        currentScope.put(name, type);
      }
    }
    else {
      if (!functionScopes.isEmpty()) {
        HashMap<String, String> currentFunctionScope = functionScopes.peek();
        if (currentFunctionScope.containsKey(name)) {
          log.warn(LoggerMessage.PARSER_FUNCTION_SEMANTIC_ERROR, name);
        } else {
          currentFunctionScope.put(name, type);
        }
      }
      else {
        if (globalVariables.containsKey(name)) {
          log.error("Variable '{}' already declared globally.", name);
        } else {
          globalVariables.put(name, type);
        }
      }
    }
  }

  public void enterFunctionScope() {
    functionScopes.push(new HashMap<>());
  }

  public void exitFunctionScope() {
    if (!functionScopes.isEmpty()) {
      functionScopes.pop();
    }
  }

  public void enterBlockScope() {
    blockScopes.push(new HashMap<>());
  }

  public void exitBlockScope() {
    if (!blockScopes.isEmpty()) {
      blockScopes.pop();
    }
  }

  private String getNodeType(ASTNode node) {
    if (node instanceof IdentifierNode) {
      return getVariableType(((IdentifierNode) node).getName());
    } else if (node instanceof LiteralNode) {
      return ((LiteralNode) node).getType();
    } else if (node instanceof BinaryOperatorNode) {
      BinaryOperatorNode binaryOp = (BinaryOperatorNode) node;
      String leftType = getNodeType(binaryOp.getLeft());
      String rightType = getNodeType(binaryOp.getRight());
      if (leftType == null || rightType == null || !leftType.equals(rightType)) {
        log.error("Type mismatch in binary operation: '{}' and '{}'.", leftType, rightType);
        return null; // Error
      }
      return leftType;
    }
    return null;
  }
}
