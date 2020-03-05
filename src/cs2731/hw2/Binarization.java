package cs2731.hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Binarization {

    ArrayList<Item> grammar;
    ArrayList<Item> lexicon;

    public Binarization() {
    }

    public void convert(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        grammar = new ArrayList<>();
        lexicon = new ArrayList<>();
        int num = 0;
        String rule = "@R.";
        String line;
        int i;
        // Grammar part
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.equals("Lexicon"))
                break;
            while (line.contains("  ")) line = line.replace("  ", " ");
            i = line.indexOf(" ");
            if (i != -1) {
                double prob = Double.parseDouble(line.substring(0, i)); // prob part
                String string = line.substring(i + 1); // grammar part
                string = string.trim();
                String[] arr = string.split("->");
                String parent = arr[0].trim();
                String[] values = arr[1].trim().split(" ");
                int len = values.length;
                if (len > 2) {
                    String addRule = rule + num++;
                    grammar.add(new Item(addRule, values[0], values[1], 1.0));
                    for (int j = 2; j < len - 1; j++) {
                        addRule = rule + num++;
                        grammar.add(new Item(addRule, rule + (num - 2), values[j], 1.0));
                    }
                    grammar.add(new Item(parent, addRule, values[len - 1], prob));
                } else {
                    if (len == 2)
                        grammar.add(new Item(parent, values[0], values[1], prob));
                    else
                        grammar.add(new Item(parent, values[0], null, prob));
                }
            }
        }

        // Lexicon part
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            i = line.indexOf(" ");
            if (i != -1) {
                double prob = Double.parseDouble(line.substring(0, i)); // prob part
                String string = line.substring(i + 1); // grammar part
                string = string.trim();
                String[] arr = string.split("->");
                String parent = arr[0].trim();
                String left = arr[1].trim();
                left = left.toLowerCase();
                lexicon.add(new Item(parent, left, null, prob));
            }
        }

        boolean modified = true;
        while (modified) {
            i = 0;
            modified = false; // keep looping if modified
            while (i < grammar.size()) {
                Item curr = grammar.get(i);
                if (curr.right == null) {
                    int j = 0;
                    boolean findOne = false;
                    while (j < grammar.size()) {
                        Item item = grammar.get(j);
                        if (item.parent.equals(curr.left)) {
                            modified = true;
                            findOne = true;
                            if (item.right != null) {
                                if (item.from != null)
                                    grammar.add(new Item(curr.parent, item.left, item.right, ":" + item.parent + item.from, curr.prob * item.prob));
                                else
                                    grammar.add(new Item(curr.parent, item.left, item.right, ":" + item.parent, curr.prob * item.prob));
                            } else {
                                boolean matchLexicon = false;
                                for (Item el : lexicon) {
                                    if (item.left.equals(el.parent)) {
                                        matchLexicon = true;
                                        grammar.add(new Item(curr.parent, el.left, null, ":" + item.parent + ":" + el.parent, curr.prob * item.prob * el.prob));
                                    }
                                }
                                if (!matchLexicon)
                                    grammar.add(new Item(curr.parent, item.left, null, ":" + item.parent, curr.prob * item.prob));
                            }
                        }
                        j++;
                    }
                    if (!findOne) {
                        for (Item el : lexicon) {
                            if (curr.left.equals(el.parent)) {
                                findOne = true;
                                grammar.add(new Item(curr.parent, el.left, null, ":" + el.parent, curr.prob * el.prob));
                            }
                        }
                    }
                    if (findOne)
                        grammar.remove(i);
                }
                i++;
            }
        }

        reader.close();

//        display(); // use for testing
    }

    private void display() {
        System.out.println("## Grammar");
        for (Item item : grammar) {
            System.out.println(item.parent + " -> " + item.left + " " + item.right + " " + item.from + " [" + item.prob + "]");
        }
        System.out.println("## Lexicon");
        for (Item item : lexicon) {
            System.out.println(item.parent + " -> " + item.left + " " + item.right + " [" + item.prob + "]");
        }
    }
}

class Item {
    String parent;
    String left;
    String right;
    String from;
    double prob;

    public Item(String parent, String left, String right, double prob) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.from = null;
        this.prob = prob;
    }

    public Item(String parent, String left, String right, String from, double prob) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.from = from;
        this.prob = prob;
    }
}