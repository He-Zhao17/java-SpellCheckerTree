package dictionary;

import javax.print.DocFlavor;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

/** CompactPrefixTree class, implements Dictionary ADT and
 *  several additional methods. Can be used as a spell checker.
 *  Fill in code and feel free to add additional methods as needed.
 *  S21 */
public class CompactPrefixTree implements Dictionary {

    private Node root; // the root of the tree

    /** Default constructor  */
    public CompactPrefixTree() {
        root = new Node();
    }

    /**
     * Creates a dictionary ("compact prefix tree")
     * using words from the given file.
     * @param filename the name of the file with words
     */
    public CompactPrefixTree(String filename) {
        // FILL IN CODE:
        // Read each word from the file, add it to the tree
        root = new Node();
        try {
            String str = new String();
            FileReader flReader = new FileReader(filename);
            Scanner scan = new Scanner(flReader);
            while (scan.hasNext()) {
                str = scan.nextLine();
                add(str);
            }
            flReader.close();
            scan.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Adds a given word to the dictionary.
     * @param word the word to add to the dictionary
     */
    public void add(String word) {
        root = add(word.toLowerCase(), root); // Calling private add method
    }

    /**
     * Checks if a given word is in the dictionary
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     */
    public boolean check(String word) {
        return check(word.toLowerCase(), root); // Calling private check method
    }

    /**
     * Checks if a given prefix is stored in the dictionary
     * @param prefix The prefix of a word
     * @return true if this prefix is a prefix of any word in the dictionary,
     * and false otherwise
     */
    public boolean checkPrefix(String prefix) {
        return checkPrefix(prefix.toLowerCase(), root); // Calling private checkPrefix method
    }

    /**
     * Returns a human-readable string representation of the compact prefix tree;
     * contains nodes listed using pre-order traversal and uses indentations to show the level of the node.
     * An asterisk after the node means the node's boolean flag is set to true.
     * The root is at the current indentation level (followed by * if the node's valid bit is set to true),
     * then there are children of the node at a higher indentation level.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // FILL IN CODE
        sb.append(treeToString(root, 0));
        return sb.toString();
    }

    /**
     * Print out the nodes of the tree to a file, using indentations to specify the level
     * of the node.
     * @param filename the name of the file where to output the tree
     */
    public void printTree(String filename) {
        // FILL IN CODE
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(this.toString());
            out.close();
        } catch (IOException e) {

        }
        // Uses toString() method; outputs info to a file
    }



    /**
     * Return an array of the entries in the dictionary that are as close as possible to
     * the parameter word.  If the word passed in is in the dictionary, then
     * return an array of length 1 that contains only that word.  If the word is
     * not in the dictionary, then return an array of numSuggestions different words
     * that are in the dictionary, that are as close as possible to the target word.
     * Implementation details are up to you, but you are required to make it efficient
     * and make good use ot the compact prefix tree.
     *
     * @param word The word to check
     * @param numSuggestions The length of the array to return.  Note that if the word is
     * in the dictionary, this parameter will be ignored, and the array will contain a
     * single world.
     * @return An array of the closest entries in the dictionary to the target word
     */

    public String[] suggest(String word, int numSuggestions) {
        // FILL IN CODE
        // Note: you need to create a private suggest method in this class
        // (like we did for methods add, check, checkPrefix)
        ResultForSuggest res = new ResultForSuggest();
        res.node = this.root;
        res.prefix = "";
        ResultForSuggest tempRes = getSuggestTree(word, res);

        if (tempRes == null) {
            //return new String[numSuggestions];
            if (word.length() == 1) {
                ArrayList<String> k = treeToList(tempRes.node, tempRes.prefix);
                String[] re = new String[numSuggestions];
                for (int i = 0; i < numSuggestions; i++) {
                    re[i] = k.get(i);
                }
                return re;
            } else {
                String tempStr = new String (word.substring(0, word.length() - 1));
                return getUpStringArr(tempStr, numSuggestions);
            }

        } else {
            if ((tempRes.prefix + tempRes.node.prefix).equals(word) && tempRes.node.isWord) {
                String[] re = new String[1];
                re[0] = word;
                return re;
            } else {
                ArrayList<String> k = treeToList(tempRes.node, tempRes.prefix);
                if (k.size() > numSuggestions) {
                    String[] re = new String[numSuggestions];
                    for (int i = 0; i < numSuggestions; i++) {
                        re[i] = k.get(i);
                    }
                    return re;
                } else {
                    String[] re = new String[numSuggestions];
                    for (int i = 0; i < k.size(); i++) {
                        re[i] = k.get(i);
                    }
                    ResultForSuggest t = new ResultForSuggest();
                    t.node = this.root;
                    t.prefix = "";
                    ResultForSuggest reUp = getSuggestTree(tempRes.prefix, t);
                    Node nodeUp = reUp.node;
                    int intChar = ((int) tempRes.node.prefix.charAt(0) - (int) 'a') % 26;
                    int index = k.size();
                    outer:
                    while (index == numSuggestions) {
                        for (int i = intChar + 1; i < 26; i++) {
                            k = treeToList(nodeUp.children[i], new String (reUp.prefix + nodeUp.prefix));
                            for (int j = 0; j < k.size(); j++) {
                                if (index == numSuggestions) {
                                    break outer;
                                }
                                re[index] = k.get(j);
                                index++;
                            }
                        }
                        for (int i = 0; i < intChar; i++) {
                            k = treeToList(nodeUp.children[i], new String (reUp.prefix + nodeUp.prefix));
                            for (int j = 0; j < k.size(); j++) {
                                if (index == numSuggestions) {
                                    break outer;
                                }
                                re[index] = k.get(j);
                                index++;
                            }
                        }
                        reUp = getSuggestTree(reUp.prefix, t);
                        intChar = ((int) nodeUp.prefix.charAt(0) - (int) 'a') % 26;
                        nodeUp = reUp.node;
                    }
                    return re;
                }
            }
        }
        //return null; // don't forget to change it
    }

    // ---------- Private helper methods ---------------
    private String[] getUpStringArr(String word, int numSuggestions) {
        ResultForSuggest res = new ResultForSuggest();
        res.node = this.root;
        res.prefix = "";
        ResultForSuggest tempRes = getSuggestTree(word, res);

        if (tempRes == null) {
            //return new String[numSuggestions];
            if (word.length() == 1) {
                ArrayList<String> k = treeToList(tempRes.node, tempRes.prefix);
                String[] re = new String[numSuggestions];
                for (int i = 0; i < numSuggestions; i++) {
                    re[i] = k.get(i);
                }
                return re;
            } else {
                String tempStr = new String (word.substring(0, word.length() - 1));
                return getUpStringArr(tempStr, numSuggestions);
            }

        } else {
            ArrayList<String> k = treeToList(tempRes.node, tempRes.prefix);
            if (k.size() > numSuggestions) {
                String[] re = new String[numSuggestions];
                for (int i = 0; i < numSuggestions; i++) {
                    re[i] = k.get(i);
                }
                return re;
            } else {
                String[] re = new String[numSuggestions];
                for (int i = 0; i < k.size(); i++) {
                    re[i] = k.get(i);
                }
                ResultForSuggest t = new ResultForSuggest();
                t.node = this.root;
                t.prefix = "";
                ResultForSuggest reUp = getSuggestTree(tempRes.prefix, t);
                Node nodeUp = reUp.node;
                int intChar = ((int) tempRes.node.prefix.charAt(0) - (int) 'a') % 26;
                int index = k.size();
                outer:
                while (index < numSuggestions) {
                    for (int i = intChar + 1; i < 26; i++) {
                        k = treeToList(nodeUp.children[i], new String (reUp.prefix + nodeUp.prefix));
                        for (int j = 0; j < k.size(); j++) {
                            if (index == numSuggestions) {
                                break outer;
                            }
                            re[index] = k.get(j);
                            index++;
                        }
                    }
                    for (int i = 0; i < intChar; i++) {
                        k = treeToList(nodeUp.children[i], new String (reUp.prefix + nodeUp.prefix));
                        for (int j = 0; j < k.size(); j++) {
                            if (index == numSuggestions) {
                                break outer;
                            }
                            re[index] = k.get(j);
                            index++;
                        }
                    }
                    reUp = getSuggestTree(reUp.prefix, t);
                    intChar = ((int) nodeUp.prefix.charAt(0) - (int) 'a') % 26;
                    nodeUp = reUp.node;
                }
                return re;
            }
        }
    }



    private ArrayList<String> treeToList(Node node, String prefix) {
        ArrayList<String> res = new ArrayList<>();
        if (node == null) {
            return res;
        }
        if (node.isWord) {
            res.add(new String(prefix + node.prefix));
        }
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                ArrayList<String> temp = treeToList(node.children[i], new String(prefix + node.prefix));
                res.addAll(temp);
            }
        }
        return res;
    }

    /**
     * Get the node of input prefix.
     * @param res the union of the tree and the had prefix.
     * @param word the input prefix;
     */
    private ResultForSuggest getSuggestTree(String word, ResultForSuggest res) {
        Node node = res.node;
        String prefix = res.prefix;
        word = word.toLowerCase();
        if (node == null) {
            return null;
        }
        if (node.prefix.length() < word.length()) {
            if (checkPrefixForNode(word, node)) {
                res.prefix = new String (prefix + node.prefix);
                word = new String(word.substring(node.prefix.length()));
                int intChar = (int) word.charAt(0) - (int) 'a';
                res.node = node.children[intChar];
                return getSuggestTree(word, res);
            } else {
                    return null;
            }
        } else {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) != node.prefix.charAt(i)) {
                    return null;
                }
            }
            return res;
        }


    }

    /**
     * Get the String of a tree.
     * @param node the tree.
     * @param numIndentations deepth of the tree;
     */
    private String treeToString(Node node, int numIndentations) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < numIndentations; i++) {
            res.append(" ");
        }
        res.append(node.prefix);
        if (node.isWord) {
            res.append("*");
        }
        res.append("\n");
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                res.append(treeToString(node.children[i], numIndentations + 1));
            }
        }
        return res.toString();
    }


    /**
     *  A private add method that adds a given string to the tree
     * @param s the string to add
     * @param node the root of a tree where we want to add a new string

     * @return a reference to the root of the tree that contains s
     */
    private Node add(String s, Node node) {
        // FILL IN CODE
        if (node == null) {
            node = new Node();
            node.prefix = s;
            node.isWord = true;
            return node;
        }
        String temp = s + "k";
        if (s.length() == node.prefix.length() && checkPrefixForNode(temp, node)) {
            if (!node.isWord) {
                node.isWord = true;
            }
            return node;
        }
        if (checkPrefixForNode(s, node)) {
            String tempStr = new String (s.substring(node.prefix.length()));
            int intChar = (int) tempStr.charAt(0) - (int) 'a';
            node.children[intChar] = add(tempStr, node.children[intChar]);
            return node;
        } else {
            StringBuilder tempStrB = new StringBuilder();
            int i  = 0;
            while (i < node.prefix.length() && i < s.length()) {
                if (node.prefix.charAt(i) == s.charAt(i)) {
                    tempStrB.append(s.charAt(i));
                    i++;
                } else {
                    break;
                }
            }
            temp = tempStrB.toString();
            Node tempNode = new Node();
            tempNode.prefix = temp;
            node.prefix = new String(node.prefix.substring(i));
            int intChar = (int) node.prefix.charAt(0) - (int) 'a';
            tempNode.children[intChar] = node;
            if (i == s.length()) {
                tempNode.isWord = true;
            } else {
                tempNode.isWord = false;
                Node newNode = new Node();
                newNode.prefix = new String(s.substring(i));
                newNode.isWord = true;
                intChar = (int) newNode.prefix.charAt(0) - (int) 'a';
                tempNode.children[intChar] = newNode;
            }
            return tempNode;
        }
        //return null; // don't forget to change it
    }


    /** A private method to check whether a given string is stored in the tree.
     *
     * @param s the string to check
     * @param node the root of a tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean check(String s, Node node) {
        // FILL IN CODE
        if (node == null) {
            return false;
        }
        if (node.isWord && node.prefix.length() == s.length()) {
            for (int i = 0; i < node.prefix.length(); i++) {
                if (node.prefix.charAt(i) != s.charAt(i)) {
                    return false;
                }
            }
            return true;
        } else {
            if (!checkPrefixForNode(s, node)) {
                return false;
            } else {
                String temp = new String (s.substring(node.prefix.length()));
                int intChar = (int) temp.charAt(0) - (int) 'a';
                return check(temp, node.children[intChar]);
            }
        }
    }

    /** A private helper method of check().
     *
     * @param word the string to check.
     * @param node the node to check.
     * @return true if the node.prefix is in the prefix of the word, false otherwise
     */
    private boolean checkPrefixForNode(String word, Node node) {
        String prefix = node.prefix;
        if (prefix.length() >= word.length()) {
            return false;
        } else {
            for (int i = 0; i < prefix.length(); i++) {
                if (prefix.charAt(i) != word.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * A private recursive method to check whether a given prefix is in the tree
     *
     * @param prefix the prefix
     * @param node the root of the tree
     * @return true if the prefix is in the dictionary, false otherwise
     */
    private boolean checkPrefix(String prefix, Node node) {
        // FILL IN CODE
        if (node == null) {
            return false;
        }
        if (node.prefix.length() >= prefix.length()) {
            for (int i = 0; i < prefix.length(); i++) {
                if (prefix.charAt(i) != node.prefix.charAt(i)) {
                    return false;
                }
            }
            return true;
        } else {
            if (checkPrefixForNode(prefix, node)) {
                String temp = new String (prefix.substring(node.prefix.length()));
                int intChar = (int) temp.charAt(0) - (int) 'a';
                return checkPrefix(temp, node.children[intChar]);
            } else {
                return false;
            }
        }
        //return false; // don't forget to change it
    }

    // You might want to create a private recursive helper method for toString
    // that takes the node and the number of indentations, and returns the tree  (printed with indentations) in a string.
    // private String toString(Node node, int numIndentations)


    // Add a private suggest method. Decide which parameters it should have

    // --------- Private class Node ------------
    // Represents a node in a compact prefix tree
    private class Node {
        String prefix; // prefix stored in the node
        Node children[]; // array of children (26 children)
        boolean isWord; // true if by concatenating all prefixes on the path from the root to this node, we get a valid word

        Node() {
            isWord = false;
            prefix = "";
            children = new Node[26]; // initialize the array of children
        }
        public String toString() {
            return NodeToString(this, 0);
        }
        public String NodeToString(Node node, int numIndentations) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < numIndentations; i++) {
                res.append(" ");
            }
            res.append(node.prefix);
            res.append("\n");
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    res.append(treeToString(node.children[i], numIndentations + 1));
                }
            }
            return res.toString();
        }



        // FILL IN CODE: Add other methods to class Node as needed
    }

    private class ResultForSuggest{
        Node node;
        String prefix;
    }

    public static void main(String[] args) {
        char c = 'a';
        int k = (int) c;
        System.out.println(k);
    }

}
