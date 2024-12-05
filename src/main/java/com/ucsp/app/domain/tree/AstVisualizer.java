package com.ucsp.app.domain.tree;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class AstVisualizer {

  private final Map<AstNode, MutableNode> nodeMap = new HashMap<>();
  private int nodeCounter = 0;

  public void visualize(AstNode root, String outputPath) {
    MutableGraph graph = mutGraph("AST").setDirected(true);
    graph.graphAttrs().add(Rank.dir(Rank.RankDir.TOP_TO_BOTTOM));

    // Convertir el AST en nodos de Graphviz
    MutableNode rootNode = convertToGraphvizNode(root);
    graph.add(rootNode);

    // Guardar el gráfico en un archivo PNG
    try {
      Graphviz.fromGraph(graph).width(800).render(Format.PNG).toFile(new File(outputPath));
      System.out.println("El AST se ha guardado en: " + outputPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private MutableNode convertToGraphvizNode(AstNode node) {
    // Crear un nodo único en Graphviz para el nodo del AST
    String label = (node.getValue() != null ? ": " + node.getValue() : "");
    MutableNode graphNode = mutNode(label + "_" + nodeCounter++).add(Label.of(label));
    nodeMap.put(node, graphNode);

    // Recorrer los hijos y añadir enlaces
    for (AstNode child : node.getChildren()) {
      MutableNode childNode = convertToGraphvizNode(child);
      graphNode.addLink(childNode);
    }

    return graphNode;
  }
}
