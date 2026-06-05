package org.micrograd.util;

import org.micrograd.core.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase();
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;

    // 1) Fast, non-cryptographic alphanumeric string (good for IDs, dummy test data)
    public static String randomAlphaNumericFast(int length) {
        if (length < 0) throw new IllegalArgumentException("length < 0");
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    public static List<Node> createListOfNodes(String baseName, String id, List<Float> input) {
        var res = new ArrayList<Node>();
        for (int i = 0; i < input.size(); i++) {
            res.add(new Node(createNodeName(baseName, id, i), input.get(i)));
        }
        return res;
    }

    public static String createNodeName(String baseName, String id, int i) {
        String prefix = baseName.isEmpty() ? "" : baseName + "_";
        return "%s%s%s".formatted(prefix, id, i + 1);
    }

    public static String createNodeName(String baseName, String id) {
        String prefix = baseName.isEmpty() ? "" : baseName + "_";
        return "%s%s".formatted(prefix, id);
    }

    public static float randomFloatInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        if (min == max) {
            return min;
        }
        float bound = Math.nextUp(max);
        return ThreadLocalRandom.current().nextFloat(min, bound);
    }
}
