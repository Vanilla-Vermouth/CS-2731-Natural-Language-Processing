package cs2731.hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class PCKY {
    private ArrayList<Item> grammar;
    private ArrayList<Item> lexicon;
    private ArrayList<ArrayList<ArrayList<Element>>> table;

    public PCKY(String filePath, String sentence, String goldStd) throws IOException {
        Binarization binarization = new Binarization();
        binarization.convert(filePath);
        grammar = binarization.grammar;
        lexicon = binarization.lexicon;
        parser(sentence.toLowerCase(), goldStd);
    }

    private void parser(String sentence, String goldStd) {
        table = new ArrayList<>();
        String[] words = sentence.split(" ");
        int len = words.length;
        for (int i = 0; i < len; i++) {
            table.add(new ArrayList<>());
            for (int j = 0; j <= i; j++) {
                table.get(i).add(new ArrayList<>());
            }
        }

        for (int i = 0; i < len; i++) {
            for (Item item : grammar) {
                if (words[i].equals(item.left)) {
                    table.get(i).get(i).add(new Element(item.parent, getBasic(words[i], item), item.prob));
                }
            }
            for (Item item : lexicon) {
                if (words[i].equals(item.left)) {
                    table.get(i).get(i).add(new Element(item.parent, getBasic(words[i], item), item.prob));
                }
            }
            for (int j = i - 1; j >= 0; j--) { // current: [i, j], current word: words[i]
                for (int k = i - 1; k >= j; k--) { // all left columns, [k, j]
                    int t = k + 1; // the corresponding column, [i, t]
                    for (Element left : table.get(k).get(j)) {
                        for (Element right : table.get(i).get(t)) {
                            for (Item item : grammar) {
                                if (item.left.equals(left.name) && item.right.equals(right.name)) {
                                    StringBuilder builder = new StringBuilder();
                                    if (item.parent.startsWith("@"))
                                        builder.append(left.path).append(right.path);
                                    else {
                                        if (item.from == null)
                                            builder.append("[").append(item.parent).append(left.path).append(right.path).append("]");
                                        else {
                                            int rightBracketAmount = 1;
                                            for (int s = 0, length = item.from.length(); s < length; s++) {
                                                if (item.from.charAt(s) == ':') rightBracketAmount++;
                                            }
                                            builder.append("[").append(item.parent).append(item.from.replace(':', '[')).append(left.path).append(right.path);
                                            while (rightBracketAmount != 0) {
                                                builder.append("]");
                                                rightBracketAmount--;
                                            }
                                        }
                                    }
                                    table.get(i).get(j).add(new Element(item.parent, builder, item.prob * left.prob * right.prob));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Output Part

        // Accept or Not?
        boolean accept = false;
        for (Element el : table.get(len - 1).get(0)) {
            if (el.name.equals("S")) {
                accept = true;
                break;
            }
        }
        if (accept)
            System.out.println("Sentence accepted");
        else
            System.out.println("Sentence rejected");

        if (accept) {
            // Sentence Probability
            double prob = 0;
            for (Element el : table.get(len - 1).get(0)) {
                if (el.name.equals("S"))
                    prob += el.prob;
            }
            System.out.println("Sentence Probability: " + prob);

            // All parse trees
            for (Element el : table.get(len - 1).get(0)) {
                if (el.name.equals("S")) {
                    System.out.println();
                    System.out.println(el.path);
                    System.out.println(el.prob);
                    evaluate(new StringBuilder(el.path), new StringBuilder(goldStd));
                }
            }
        }

//        display(len); // use for testing
    }

    private void evaluate(StringBuilder expr, StringBuilder goldStd) {
        ArrayList<String> exprList = generateStructureList(expr);
        ArrayList<String> goldStdList = generateStructureList(goldStd);
        HashSet<String> goldStdSet = new HashSet<>(goldStdList);
        int cnt = 0; // count for matching gold standard
        for (String el : exprList) {
            if (goldStdSet.contains(el)) cnt++;
        }
//        System.out.println(exprList);
//        System.out.println(goldStdList);
        double precision = (double) cnt / exprList.size();
        double recall = (double) cnt / goldStdSet.size();
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
    }

    private ArrayList<String> generateStructureList(StringBuilder expr) {
        ArrayList<String> exprList = new ArrayList<>();
        bracketHandler(expr);
//		System.out.println(expr);
        int ntStart;
        int bracket; // 0 - closed
        for (int i = 0, len = expr.length(); i < len; i++) {
            bracket = 0;
            if (expr.charAt(i) == '[') {
                bracket++;
                ntStart = i + 1;
                int j;
                for (j = ntStart; j < len; j++) if (expr.charAt(j) == '[' || expr.charAt(j) == '(') break;
                i = j - 1;
                String nt = expr.substring(ntStart, j).trim();
//				System.out.println(nt);
                int start = 0;
                int end = 0;
                String left = null;
                String right = null;
                boolean closed = false;
                for (j = j - 1; j < len; j++) {
                    if (bracket == 0) break;
                    if (expr.charAt(j) == '[') bracket++;
                    else if (expr.charAt(j) == ']') bracket--;
                    else if (expr.charAt(j) == '(') start = j + 1;
                    else if (expr.charAt(j) == ')') {
                        end = j;
                        closed = true;
                    }
                    if (closed) {
                        String num = expr.substring(start, end);
                        if (left == null) left = num;
                        else right = num;
                        closed = false;
                    }
                }
                if (bracket == 0) {
                    if (right != null) exprList.add(nt + "(" + left + "," + (Integer.parseInt(right) + 1) + ")");
                    else exprList.add(nt + "(" + left + "," + (Integer.parseInt(left) + 1) + ")");
                }
            }
        }
//		System.out.println(exprList);
        return exprList;
    }

    private StringBuilder bracketHandler(StringBuilder expr) {
        // replace [NT word] => (#)
        boolean notClosed = true;
        int start = 0;
        int num = 0;
        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '[') {
                start = i;
                notClosed = true;
            } else if (expr.charAt(i) == ']' && notClosed) {
                expr.delete(start, i + 1);
                expr.insert(start, "(" + num++ + ")");
                notClosed = false;
                i = start + 2;
            }
        }
        return expr;
    }

    private StringBuilder getBasic(String word, Item item) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(item.parent);
        int rightBracketAmount = 1;
        if (item.from != null) {
            for (int i = 0, len = item.from.length(); i < len; i++) {
                if (item.from.charAt(i) == ':') rightBracketAmount++;
            }
            builder.append(item.from.replace(':', '['));
        }
        builder.append(" ").append(word);
        while (rightBracketAmount != 0) {
            builder.append("]");
            rightBracketAmount--;
        }
        return builder;
    }

    private void display(int len) {
        System.out.println("## Cell Content");
        for (int i = 0; i < len; i++) {
            for (int j = 0; j <= i; j++) {
                System.out.println("# (" + i + "," + j + ")");
                for (Element el : table.get(i).get(j)) {
                    System.out.println(el.name + " " + el.path);
                }
            }
        }
    }
}

class Element {
    String name;
    StringBuilder path;
    double prob;

    public Element(String name, StringBuilder path, double prob) {
        this.name = name;
        this.path = path;
        this.prob = prob;
    }
}