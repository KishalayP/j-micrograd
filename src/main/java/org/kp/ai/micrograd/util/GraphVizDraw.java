package org.kp.ai.micrograd.util;

import org.kp.ai.micrograd.core.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Lightweight Graphviz (DOT) exporter for Node graphs.
 * <p>
 * This replaces the previous ASCII renderer and emits DOT text suitable
 * for rendering with Graphviz (dot, neato, etc.). The output uses a
 * left-to-right layout (rankdir=LR) and box-shaped nodes. Edge labels
 * carry the node's operator when present.
 */
public class GraphVizDraw {

    public static String getGraphDisplayString(Node head) {
        if (head == null) return "";
        List<Node> nodes = new ArrayList<>();
        collectPostOrder(head, nodes, new HashSet<>());
        String dot = renderDot(nodes);
        // Try to render using Graphviz `dot` if available; if not, return DOT text
        try {
            if (!isDotAvailable()) return dot;
            // write DOT to temp file
            Path dotFile = Files.createTempFile("micrograd-graph", ".dot");
            Files.writeString(dotFile, dot, StandardOpenOption.TRUNCATE_EXISTING);
            Path outPng = Files.createTempFile("micrograd-graph", ".png");
            // run: dot -Tpng -o outPng dotFile
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "-o", outPng.toString(), dotFile.toString());
            Process p = pb.start();
            int rc = p.waitFor();
            if (rc == 0 && Files.exists(outPng)) {
                // Only save the image and return its path; do not auto-open.
                return "Rendered graph to: " + outPng.toAbsolutePath();
            } else {
                return dot; // fallback
            }
        } catch (IOException | InterruptedException e) {
            return dot;
        }
    }

    private static boolean isDotAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-V");
            Process p = pb.start();
            // dot prints version to stderr; wait briefly
            int rc = p.waitFor();
            return rc == 0 || rc == 1 || rc == 2; // some dot versions return non-zero
        } catch (Throwable t) {
            return false;
        }
    }

    // Note: we intentionally do not auto-open generated images. Consumers
    // can open the printed file path if they wish.

    private static String renderDot(List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("  rankdir=LR;\n");
        sb.append("  node [shape=box, fontname=\"Monospace\"];\n");

        // assign stable ids per run using identity hash
        Map<Node, String> ids = new IdentityHashMap<>();
        int i = 0;
        for (Node n : nodes) {
            ids.put(n, "n" + (i++));
        }

        // emit nodes with labels
        for (Node n : nodes) {
            String id = ids.get(n);
            String label = n.toString().replace("\"", "\\\"");
            sb.append("  ").append(id).append(" [label=")
                    .append('\"').append(label).append('\"').append("];\n");
        }

        // emit edges child -> parent and include operator label when present
        for (Node n : nodes) {
            if (n.prev == null) continue;
            for (Node child : n.prev) {
                String from = ids.get(child);
                String to = ids.get(n);
                sb.append("  ").append(from).append(" -> ").append(to);
                if (n.operatorFunction != null) {
                    String op = ("[" + n.operatorFunction + "]").replace("\"", "\\\"");
                    sb.append(" [label=\"").append(op).append("\"]");
                }
                sb.append(";\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static void collectPostOrder(Node node, List<Node> nodes, Set<Node> visited) {
        if (node == null || visited.contains(node)) return;
        if (node.prev != null && !node.prev.isEmpty()) {
            for (Node child : node.prev) {
                collectPostOrder(child, nodes, visited);
            }
        }
        visited.add(node);
        nodes.add(node);
    }
}
