package org.kp.ai.micrograd.util;

import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.api.MathFunction;
import org.kp.ai.micrograd.functions.registry.MathFunctions;
import org.kp.ai.micrograd.model.Position;

import java.util.*;

public class DrawGraph {

    private static class ArrowLabel {
        int x;
        int y;
        String label;

        ArrowLabel(int x, int y, String label) {
            this.x = x;
            this.y = y;
            this.label = label;
        }
    }

    public static String getGraphDisplayString(Node head) {
        // Collect all nodes in post-order
        List<Node> nodes = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        collectPostOrder(head, nodes, visited);

        // Pass empty map; DrawGraph will recalculate positions for left-to-right layout
        Map<Node, Position> positions = new HashMap<>();
        return DrawGraph.renderGraph(nodes);
    }

    // Build ASCII graph grid (left-to-right layout)
    private static String renderGraph(List<Node> nodes) {
        if (nodes.isEmpty()) return "";

        // The last node in post-order is the root (head)
        Node head = nodes.getLast();

        // Compute positions with a left-to-right layout where operator/parent nodes
        // are placed in front of (to the right of) their operand nodes and aligned
        // vertically with them. This makes binary operations like x2*w2 appear
        // inline/in-front of x2 and w2 and prevents arrows from crossing many nodes.
        Map<Node, Position> positions = new HashMap<>();

        // Map to cache each node's full drawn box width (including borders)
        Map<Node, Integer> boxWidths = new HashMap<>();
        for (Node n : nodes) {
            boxWidths.put(n, n.toString().length() + 4); // content + padding + borders
        }

        // Vertical placement counter for leaves (nodes with no operands)
        int[] leafCounter = new int[]{0};
        int verticalSpacing = 4;
        int horizontalSpacing = 6;

        // Recursively compute positions starting from head
        computePositions(head, positions, boxWidths, leafCounter, verticalSpacing, horizontalSpacing, new HashSet<>());

        // Post-process leaf nodes (e.g., bias) so they appear near their consumer.
        // Specifically, place a leaf used by an ADD node below that ADD node so it
        // visually appears as the bias input to the sum. For other consumers,
        // align vertically with the consumer.
        for (Node leaf : nodes) {
            if (leaf.prev != null && !leaf.prev.isEmpty()) continue; // not a leaf

            Node chosenConsumer = null;
            Position chosenPos = null;
            for (Node cand : nodes) {
                if (cand.prev != null && cand.prev.contains(leaf)) {
                    Position cp = positions.get(cand);
                    if (cp == null) continue;
                    // prefer the left-most consumer (smallest x) so the leaf sits
                    // immediately before its use in a left-to-right layout
                    if (chosenConsumer == null || cp.x > chosenPos.x) {
                        chosenConsumer = cand;
                        chosenPos = cp;
                    }
                }
            }

            if (chosenConsumer != null) {
                int w = boxWidths.getOrDefault(leaf, leaf.toString().length() + 4);
                int x = Math.max(0, chosenPos.x - horizontalSpacing - w);
                int y;
                if (chosenConsumer.operatorFunction == MathFunctions.ADD) {
                    // place bias below the add node
                    y = chosenPos.y + verticalSpacing;
                } else {
                    y = chosenPos.y;
                }
                positions.put(leaf, new Position(x, y));
            } else {
                // fallback: ensure the leaf has a position
                if (!positions.containsKey(leaf)) {
                    int x = 0;
                    int y = leafCounter[0] * verticalSpacing;
                    positions.put(leaf, new Position(x, y));
                    leafCounter[0]++;
                }
            }
        }

        // Resolve collisions for all nodes so boxes don't overlap. Iterate nodes
        // in increasing x order and push a node down until it doesn't overlap any
        // previously fixed node.
        List<Node> ordered = new ArrayList<>(positions.keySet());
        ordered.sort(Comparator.comparingInt(n -> positions.get(n).x));
        List<Node> fixed = new ArrayList<>();
        for (Node n : ordered) {
            Position p = positions.get(n);
            if (p == null) continue;
            int w = boxWidths.getOrDefault(n, n.toString().length() + 4);
            int h = 3;
            boolean moved;
            do {
                moved = false;
                for (Node other : fixed) {
                    Position op = positions.get(other);
                    if (op == null) continue;
                    int ow = boxWidths.getOrDefault(other, other.toString().length() + 4);
                    int oh = 3;
                    boolean horizOverlap = p.x < (op.x + ow) && (p.x + w) > op.x;
                    boolean vertOverlap = p.y < (op.y + oh) && (p.y + h) > op.y;
                    if (horizOverlap && vertOverlap) {
                        p = new Position(p.x, p.y + verticalSpacing);
                        positions.put(n, p);
                        moved = true;
                        break;
                    }
                }
            } while (moved);
            fixed.add(n);
        }

        // Calculate grid dimensions based on computed positions and box widths
        int gridWidth = 0;
        int gridHeight = 0;
        for (Map.Entry<Node, Position> e : positions.entrySet()) {
            Node n = e.getKey();
            Position p = e.getValue();
            int w = boxWidths.getOrDefault(n, n.toString().length() + 4);
            gridWidth = Math.max(gridWidth, p.x + w + 5);
            gridHeight = Math.max(gridHeight, p.y + 5);
        }

        char[][] grid = new char[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++) {
            Arrays.fill(grid[i], ' ');
        }

        // Draw edges first but collect operator labels to draw after nodes so
        // labels are not overwritten by node boxes.
        List<ArrowLabel> arrowLabels = new ArrayList<>();
        for (Node node : nodes) {
            if (node.prev != null && !node.prev.isEmpty()) {
                Position toPos = positions.get(node);
                for (Node child : node.prev) {
                    Position fromPos = positions.get(child);
                    if (fromPos != null && toPos != null) {
                        drawArrow(grid, child, node, fromPos, toPos, arrowLabels);
                    }
                }
            }
        }

        // Draw nodes in the original order. Collision resolution should prevent
        // overlaps; drawing in post-order keeps boxes in a predictable layout.
        for (Node node : nodes) {
            Position pos = positions.get(node);
            if (pos != null) {
                drawNodeBox(grid, node, pos.x, pos.y);
            }
        }

        // Now draw collected operator labels on top of arrows so they remain visible
        for (ArrowLabel al : arrowLabels) {
            drawString(grid, al.label, al.x, al.y);
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

    // Compute positions such that parent/operator nodes appear to the right (in front)
    // of their operand nodes and aligned vertically. Leaves are stacked top-to-bottom.
    private static void computePositions(Node node,
                                         Map<Node, Position> positions,
                                         Map<Node, Integer> boxWidths,
                                         int[] leafCounter,
                                         int vSpacing,
                                         int hSpacing,
                                         Set<Node> visited) {
        if (visited.contains(node)) return;
        visited.add(node);

        if (node.prev == null || node.prev.isEmpty()) {
            // Leaf: assign next vertical slot at x = 0
            int x = 0;
            int y = leafCounter[0] * vSpacing;
            positions.put(node, new Position(x, y));
            leafCounter[0]++;
            return;
        }

        // Compute children's positions first
        for (Node child : node.prev) {
            computePositions(child, positions, boxWidths, leafCounter, vSpacing, hSpacing, visited);
        }

        // Place this node to the right of its rightmost child, aligned vertically
        int maxRight = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (Node child : node.prev) {
            Position cp = positions.get(child);
            if (cp == null) continue;
            int w = boxWidths.getOrDefault(child, child.toString().length() + 4);
            maxRight = Math.max(maxRight, cp.x + w);
            minY = Math.min(minY, cp.y);
        }

        if (maxRight == Integer.MIN_VALUE) {
            // Fallback to leaf behavior
            positions.put(node, new Position(0, leafCounter[0] * vSpacing));
            leafCounter[0]++;
        } else {
            int x = maxRight + hSpacing;
            int y = minY; // align with top-most child for inline appearance
            positions.put(node, new Position(x, y));
        }
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

    private static void drawArrow(char[][] grid, Node fromNode, Node toNode, Position from, Position to, List<ArrowLabel> labels) {
        // Draw from node center right to target node center left
        int fromX = from.x + fromNode.toString().length() + 4; // right edge of source box (+2 for padding, +2 for borders)
        int fromY = from.y + 1;   // middle of source box (compact)
        int toX = to.x;           // left edge of target box
        int toY = to.y + 1;       // middle of target box (compact)

        if (fromX >= toX) return;

        // Draw horizontal line to mid-point (choose mid biased left) so we
        // have a clear intersection column for the vertical connector.
        int midX = (fromX + toX - 1) / 2;
        for (int x = fromX; x <= midX; x++) {
            drawCharAt(grid, x, fromY, '-');
        }

        // Draw vertical line. Use '+' at the intersection points so corners
        // visually connect to horizontal segments, and '|' elsewhere.
        int minY = Math.min(fromY, toY);
        int maxY = Math.max(fromY, toY);
        for (int y = minY; y <= maxY; y++) {
            if (y == fromY || y == toY) {
                drawCharAt(grid, midX, y, '+');
            } else {
                drawCharAt(grid, midX, y, '|');
            }
        }

        // Draw horizontal line from mid-point to target, leaving space for a
        // label just before the arrow head if present. Start at midX so the
        // horizontal segment visually connects to the '+' we placed above.
        MathFunction oper = toNode.operatorFunction;
        String label = null;
        int labelLen = 0;
        if (oper != null) {
            label = "[" + oper + "]";
            labelLen = label.length();
        }

        int arrowHeadX = toX - 1;
        int segStart = midX; // include the mid column so label/dashes connect
        int segEnd = arrowHeadX - 1; // space available for dashes/label
        int available = segEnd - segStart + 1;

        // Make arrow length dynamic to accommodate the operator label (including
        // brackets). If there's not enough space in the default segment, try to
        // shift segStart left as far as the source horizontal allows so the label
        // can fit just before the arrow head.
        if (label != null && available < labelLen) {
            int candidateStart = Math.max(fromX, arrowHeadX - labelLen - 1);
            int candidateAvailable = segEnd - candidateStart + 1;
            if (candidateAvailable >= labelLen) {
                segStart = candidateStart;
                available = candidateAvailable;
            }
            // if still not enough space, we'll fall back later to alternate placement
        }

        if (label != null && available >= labelLen) {
            // Try to ensure a minimum padding of dashes on both sides of the label
            final int minPad = 3; // minimum dashes before and after label when possible
            int required = labelLen + minPad * 2;
            // If not enough room, try to shift segStart left as far as possible (but not past fromX)
            if (available < required) {
                int need = required - available;
                segStart = Math.max(fromX, segStart - need);
                available = segEnd - segStart + 1;
            }

            // Now if we have enough for desired padding, center the label within the
            // region leaving at least minPad on each side. Otherwise fall back to
            // centering with whatever padding is available.
            int labelStart;
            if (available >= required) {
                labelStart = segStart + minPad; // leave minPad left dashes
                // if there is extra room beyond required, center the label in the extra space
                int extra = available - required;
                labelStart += extra / 2;
            } else {
                // not enough for minPad on both sides, center label in available
                labelStart = segStart + (available - labelLen) / 2;
            }

            // draw dashes left of label
            for (int x = segStart; x < labelStart; x++) {
                drawCharAt(grid, x, toY, '-');
            }
            // draw dashes right of label
            for (int x = labelStart + labelLen; x <= segEnd; x++) {
                drawCharAt(grid, x, toY, '-');
            }
            // schedule label to be drawn after nodes
            labels.add(new ArrowLabel(labelStart, toY, label));
        } else {
            // no space for label in-line; draw full dashes up to arrow head
            for (int x = segStart; x <= segEnd; x++) {
                drawCharAt(grid, x, toY, '-');
            }
            if (label != null) {
                // fallback: put label above the vertical segment if possible
                int altY = Math.max(0, minY - 1);
                int altX = Math.max(0, midX - labelLen / 2);
                if (altX + labelLen < grid[0].length) {
                    labels.add(new ArrowLabel(altX, altY, label));
                }
            }
        }

        // Arrow head
        drawCharAt(grid, arrowHeadX, toY, '>');
    }

    private static void drawCharAt(char[][] grid, int x, int y, char c) {
        if (y >= 0 && y < grid.length && x >= 0 && x < grid[y].length) {
            grid[y][x] = c;
        }
    }
}
