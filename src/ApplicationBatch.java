import java.io.*;
import java.util.*;

public class ApplicationBatch {
    final static int DELAY = 0;
    final static int CA2D_GENERATIONS = 50;

    public static void main(String[] args) throws Exception {
        runRandom2DBSRules();
        System.exit(0);
    }

    // ~20 random 1D rules with k = 3, 5, 7, 9 (5 per k value)
    private static void runRandom1DRules() throws Exception {
        int[] kValues = {3, 5, 7, 9};
        int rulesPerK = 5;
        int total = kValues.length * rulesPerK;
        int count = 0;

        for (int k : kValues) {
            for (int i = 1; i <= rulesPerK; i++) {
                count++;
                String ruleName = "1D_regle_k" + k + "_" + i;
                System.out.println("[" + count + "/" + total + "] Running k=" + k + " rule " + i);
                runCA1DRandom(k);
            }
        }
        System.out.println("All 1D random rules completed.");
    }

    // Random 2D B/S rules: k=9,25, 2 rules per k, rect + rand(0.2) init
    private static void runRandom2DBSRules() throws Exception {
        int[] kValues = {9, 25};
        int rulesPerK = 2;
        int total = kValues.length * rulesPerK * 2;
        int count = 0;
        Random rand = new Random();

        for (int k : kValues) {
            for (int r = 1; r <= rulesPerK; r++) {
                String bsString = randomBSString(rand, k);
                String bsDesc = bsString.replace("/", "_");

                count++;
                System.out.println("[" + count + "/" + total + "] 2D k=" + k + " rule " + r + " rect: " + bsString);
                runCA2DGenRules(k, bsString, "2D_" + bsDesc + "_k" + k + "_rect", false);

                count++;
                System.out.println("[" + count + "/" + total + "] 2D k=" + k + " rule " + r + " rand: " + bsString);
                runCA2DGenRules(k, bsString, "2D_" + bsDesc + "_k" + k + "_rand", true);
            }
        }
        System.out.println("All 2D B/S random rules completed.");
    }

    private static String randomBSString(Random rand, int knn) {
        StringBuilder born = new StringBuilder("B");
        StringBuilder survive = new StringBuilder("S");
        boolean firstB = true, firstS = true;
        for (int i = 0; i < knn; i++) {
            if (rand.nextBoolean()) {
                if (!firstB) born.append(",");
                born.append(i); firstB = false;
            }
            if (rand.nextBoolean()) {
                if (!firstS) survive.append(",");
                survive.append(i); firstS = false;
            }
        }
        return born.toString() + "/" + survive.toString();
    }

    private static void runCA2DGenRules(int k, String bsString, String ruleName, boolean randInit) throws Exception {
        CA2D ca = new CA2D(500, 500, 2, k);
        ca.ruleString = ruleName;
        ca.rules.genRules(bsString);

        if (randInit) ca.init(0.2);
        else ca.rectangleCenter(3, 5);
        ca.displayGridImage();

        for (int gen = 0; gen < CA2D_GENERATIONS; gen++) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }

    // Random 2D rules: k=9,25,49, 2 rules per k, rect + rand(0.2) init
    private static void runRandom2DRules() throws Exception {
        int[] kValues = {9, 25, 49};
        int rulesPerK = 2;
        int total = kValues.length * rulesPerK * 2;
        int count = 0;

        for (int k : kValues) {
            int ruleSize = Math.max(1, (int) Math.min(Math.pow(2, k - 5), 1048576.0));
            for (int r = 1; r <= rulesPerK; r++) {
                int[] ruleInts = Rule.randomRules(ruleSize);
                String ruleDesc = buildRuleDesc(ruleInts, k);

                count++;
                System.out.println("[" + count + "/" + total + "] 2D k=" + k + " rule " + r + " rect");
                runCA2DRandom(k, ruleInts, ruleDesc + "_rect");

                count++;
                System.out.println("[" + count + "/" + total + "] 2D k=" + k + " rule " + r + " rand");
                runCA2DRandom(k, ruleInts, ruleDesc + "_rand");
            }
        }
        System.out.println("All 2D random rules completed.");
    }

    private static String buildRuleDesc(int[] ruleInts, int k) {
        StringBuilder sb = new StringBuilder("2D");
        int display = Math.min(ruleInts.length, 16);
        for (int i = 0; i < display; i++)
            sb.append("_").append(String.valueOf(ruleInts[i]).replace("-", "n"));
        if (ruleInts.length > 16) sb.append("_k").append(k);
        else sb.append("_k").append(k);
        return sb.toString();
    }

    private static void runCA2DRandom(int k, int[] ruleInts, String ruleName) throws Exception {
        CA2D ca = new CA2D(500, 500, 2, k);
        ca.ruleString = ruleName;
        ca.rules.setRules(ruleInts);

        if (ruleName.endsWith("_rect")) ca.rectangleCenter(3, 5);
        else ca.init(0.2);

        ca.displayGridImage();
        for (int gen = 0; gen < CA2D_GENERATIONS; gen++) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }

    // 2D rules from rules.txt with rectangle center init
    private static void run2DRulesFromFile() throws Exception {
        List<String> lines = readLines("src/rules.txt");

        // Line 26 (0-indexed 25): 2D CA rule (16 ints, knn=9)
        if (lines.size() > 25) {
            String line26 = lines.get(25).trim();
            if (!line26.isEmpty()) {
                int[] rule2D = parseInts(line26);
                System.out.println("[1/3] Running 2D int rule (rectangle center)");
                runCA2D(rule2D, "2D_int_rule_rect");
            }
        }

        // Lines 29-30 (0-indexed 28-29): B/S rule strings
        int ruleStrIndex = 2;
        for (int i = 28; i < lines.size(); i++) {
            String ruleStr = lines.get(i).trim();
            if (ruleStr.isEmpty() || ruleStr.startsWith("Cool")) continue;
            System.out.println("[" + ruleStrIndex + "/3] Running rule string (rectangle center): " + ruleStr);
            runCA2DString(ruleStr);
            ruleStrIndex++;
        }
        System.out.println("All 2D rules completed.");
    }

    // All rules from rules.txt (original batch)
    private static void runAllRulesFromFile() throws Exception {
        List<String> lines = readLines("src/rules.txt");

        // Lines 1-24 (0-indexed 0-23): 1D CA rules (4 ints each, knn=7)
        for (int i = 0; i < 24 && i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            int[] ruleInts = parseInts(line);
            String ruleName = "1D_" + line.trim().replaceAll("\\s+", "_").replace("-", "n");
            System.out.println("[" + (i + 1) + "/26] Running 1D rule " + (i + 1) + ": " + line);
            runCA1D(ruleInts, ruleName);
        }

        // Line 26 (0-indexed 25): 2D CA rule (16 ints, knn=9)
        if (lines.size() > 25) {
            String line26 = lines.get(25).trim();
            if (!line26.isEmpty()) {
                int[] rule2D = parseInts(line26);
                System.out.println("[25/26] Running 2D int rule");
                runCA2D(rule2D, "2D_int_rule");
            }
        }

        // Lines 29-30 (0-indexed 28-29): B/S rule strings
        int ruleStrIndex = 26;
        for (int i = 28; i < lines.size(); i++) {
            String ruleStr = lines.get(i).trim();
            if (ruleStr.isEmpty() || ruleStr.startsWith("Cool")) continue;
            System.out.println("[" + ruleStrIndex + "/26] Running rule string: " + ruleStr);
            runCA2DString(ruleStr);
            ruleStrIndex++;
        }
        System.out.println("All rules completed.");
    }

    private static List<String> readLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);
        }
        return lines;
    }

    private static int[] parseInts(String line) {
        String[] parts = line.trim().split("\\s+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++)
            result[i] = Integer.parseInt(parts[i]);
        return result;
    }

    private static void runCA1D(int[] ruleInts, String ruleName) throws Exception {
        CA1D ca = new CA1D(1200, 720, 1, 7);
        ca.ruleString = ruleName;
        ca.rules.setRules(ruleInts);
        ca.init();
        ca.displayGridImage();

        while (!ca.isFinished) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }

    private static void runCA1DRandom(int k) throws Exception {
        int ruleSize = Math.max(1, (int) Math.pow(2, k - 5));
        int[] ruleInts = Rule.randomRules(ruleSize);

        StringBuilder sb = new StringBuilder("1D");
        for (int v : ruleInts)
            sb.append("_").append(String.valueOf(v).replace("-", "n"));
        sb.append("_k").append(k);
        String ruleName = sb.toString();

        CA1D ca = new CA1D(1200, 720, 1, k);
        ca.ruleString = ruleName;
        ca.rules.setRules(ruleInts);
        ca.init();
        ca.displayGridImage();

        while (!ca.isFinished) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }

    private static void runCA2D(int[] ruleInts, String ruleName) throws Exception {
        CA2D ca = new CA2D(500, 500, 2, 9);
        ca.ruleString = ruleName;
        ca.rules.setRules(ruleInts);
        ca.rectangleCenter(3, 5);
        ca.displayGridImage();

        for (int gen = 0; gen < CA2D_GENERATIONS; gen++) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }

    private static void runCA2DString(String ruleStr) throws Exception {
        CA2D ca = new CA2D(500, 500, 2, 25);
        ca.ruleString = ruleStr.replaceAll("[/\\\\:*?\"<>|]", "_") + "_rect";
        ca.rules.genRules(ruleStr);
        ca.rectangleCenter(3, 5);
        ca.displayGridImage();

        for (int gen = 0; gen < CA2D_GENERATIONS; gen++) {
            ca.nextGeneration();
            ca.displayGridImage();
        }
        ca.saveGridImage();
        ca.imageFrame.dispose();
    }
}
