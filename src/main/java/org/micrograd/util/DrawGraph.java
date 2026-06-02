package org.micrograd.util;

import org.micrograd.core.Node;
import org.micrograd.functions.MathFunctions;

import java.util.*;

public class DrawGraph {

    public static String getGraphDisplayString(Node head) {
        // Collect all nodes in post-order
        List<Node> nodes = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        collectPostOrder(head, nodes, visited);

        // Pass empty map; DrawGraph will recalculate positions for left-to-right layout
        Map<Node, Position> positions = new HashMap<>();
        return DrawGraph.renderGraph(nodes, positions);
    }

    // Build ASCII graph grid (left-to-right layout)
    private static String renderGraph(List<Node> nodes, Map<Node, Position> positionsMap) {
        if (nodes.isEmpty()) return "";

        // The last node in post-order is the root (head)
        Node head = nodes.get(nodes.size() - 1);

        // Calculate levels based on distance from root to align nodes correctly
        Map<Node, Integer> nodeLevels = new HashMap<>();
        Map<Node, Integer> distFromRoot = new HashMap<>();
        calculateDistFromRoot(head, 0, distFromRoot);

        int maxDist = distFromRoot.values().stream().max(Integer::compare).orElse(0);
        Map<Integer, List<Node>> levelNodes = new HashMap<>();
        int maxLevel = 0;

        for (Node node : nodes) {
            int level = maxDist - distFromRoot.getOrDefault(node, 0) + 1;
            levelNodes.computeIfAbsent(level, k -> new ArrayList<>()).add(node);
            maxLevel = Math.max(maxLevel, level);
        }

        // Create positions based on levels (X) and vertical stacking (Y)
        Map<Node, Position> positions = new HashMap<>();
        int currentX = 0;
        int verticalSpacing = 4;

        for (int level = 1; level <= maxLevel; level++) {
            List<Node> nodesAtLevel = levelNodes.getOrDefault(level, new ArrayList<>());
            int maxLevelWidth = 0;
            for (int i = 0; i < nodesAtLevel.size(); i++) {
                Node node = nodesAtLevel.get(i);
                positions.put(node, new Position(currentX, i * verticalSpacing));
                maxLevelWidth = Math.max(maxLevelWidth, node.toString().length() + 15);
            }
            currentX += maxLevelWidth;
        }

        // Calculate grid dimensions
        int gridWidth = currentX + 20;
        int gridHeight = 0;
        for (Position p : positions.values()) {
            gridHeight = Math.max(gridHeight, p.y + 5);
        }

        char[][] grid = new char[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++) {
            Arrays.fill(grid[i], ' ');
        }

        // Draw edges first
        for (Node node : nodes) {
            if (node.prev != null && !node.prev.isEmpty()) {
                Position toPos = positions.get(node);
                for (Node child : node.prev) {
                    Position fromPos = positions.get(child);
                    if (fromPos != null && toPos != null) {
                        drawArrow(grid, child, node, fromPos, toPos);
                    }
                }
            }
        }

        // Draw nodes
        for (Node node : nodes) {
            Position pos = positions.get(node);
            if (pos != null) {
                drawNodeBox(grid, node, pos.x, pos.y);
            }
        }

        // Convert grid to string
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            String line = new String(row).replaceAll(" +$", "");
            if (!line.isEmpty()) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString().replaceAll("\n+$", "");
    }

    private static void calculateDistFromRoot(Node node, int dist, Map<Node, Integer> dists) {
        dists.put(node, Math.max(dists.getOrDefault(node, 0), dist));
        if (node.prev != null) {
            for (Node prev : node.prev) {
                calculateDistFromRoot(prev, dist + 1, dists);
            }
        }
    }

    private static int calculateDepth(Node node, Map<Node, Integer> depths) {
        if (depths.containsKey(node)) {
            return depths.get(node);
        }

        int maxChildDepth = 0;
        if (node.prev != null && !node.prev.isEmpty()) {
            for (Node child : node.prev) {
                maxChildDepth = Math.max(maxChildDepth, calculateDepth(child, depths));
            }
        }

        int depth = maxChildDepth + 1;
        depths.put(node, depth);
        return depth;
    }

    private static void collectPostOrder(Node node, List<Node> nodes, Set<Node> visited) {
        if (visited.contains(node)) return;
        if (node.prev != null && !node.prev.isEmpty()) {
            for (Node child : node.prev) {
                collectPostOrder(child, nodes, visited);
            }
        }
        visited.add(node);
        nodes.add(node);
    }

    private static void drawNodeBox(char[][] grid, Node node, int x, int y) {
        // Get formatted display from node's toString() method
        String nodeStr = node.toString();

        // Calculate box width dynamically based on string length
        int boxWidth = nodeStr.length() + 2; // +2 for padding

        // Create dynamic borders for a compact enclosed box
        String border = "+" + "-".repeat(boxWidth) + "+";
        String content = "| " + nodeStr + " |";

        drawString(grid, border, x, y);
        drawString(grid, content, x, y + 1);
        drawString(grid, border, x, y + 2);
    }

    private static void drawString(char[][] grid, String str, int x, int y) {
        if (y >= 0 && y < grid.length) {
            for (int i = 0; i < str.length(); i++) {
                if (x + i >= 0 && x + i < grid[y].length) {
                    grid[y][x + i] = str.charAt(i);
                }
            }
        }
    }

    private static void drawArrow(char[][] grid, Node fromNode, Node toNode, Position from, Position to) {
        // Draw from node center right to target node center left
        int fromX = from.x + fromNode.toString().length() + 4; // right edge of source box (+2 for padding, +2 for borders)
        int fromY = from.y + 1;   // middle of source box (compact)
        int toX = to.x;           // left edge of target box
        int toY = to.y + 1;       // middle of target box (compact)

        if (fromX >= toX) return;

        // Draw horizontal line to mid-point
        int midX = (fromX + toX) / 2;
        for (int x = fromX; x <= midX; x++) {
            drawCharAt(grid, x, fromY, '-');
        }

        // Draw vertical line
        int minY = Math.min(fromY, toY);
        int maxY = Math.max(fromY, toY);
        for (int y = minY; y <= maxY; y++) {
            drawCharAt(grid, midX, y, '|');
        }

        // Draw horizontal line from mid-point to target
        for (int x = midX; x < toX; x++) {
            drawCharAt(grid, x, toY, '-');
        }

        // Draw operation label on the arrow
        MathFunctions oper = toNode.operatorFunction;
        if (oper != null && !oper.toString().isEmpty()) {
            int labelX = (midX + toX) / 2 - (oper.toString().length() / 2);
            if (labelX > midX && labelX + oper.toString().length() < toX) {
                drawString(grid, "[" + oper + "]", labelX - 1, toY);
            }
        }

        // Arrow head
        drawCharAt(grid, toX - 1, toY, '>');
    }

    private static void drawCharAt(char[][] grid, int x, int y, char c) {
        if (y >= 0 && y < grid.length && x >= 0 && x < grid[y].length) {
            grid[y][x] = c;
        }
    }
}
