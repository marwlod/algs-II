package io.github.marwlod.boggle;

public class RWayTrie {
    // suited for strings containing only uppercase letters of english alphabet
    private static final int R = 26;
    private Node root;

    private static class Node {
        private boolean present;
        // 'A' has index 0, 'Z' index 25
        private Node[] next = new Node[R];
    }

    public RWayTrie() {
    }

    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node trav = root;
        int d = 0;
        while (trav != null) {
            if (d == key.length() && trav.present) return true;
            // there is key with this prefix, but this particular key is not present
            else if (d == key.length()) return false;
            char c = key.charAt(d++);
            trav = trav.next[c-'A'];
        }
        return false;
    }

    // are there any words in the trie that have given prefix?
    public boolean hasWordsWithPrefix(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("argument to hasWordsWithPrefix() is null");
        Node trav = root;
        int d = 0;
        while (trav != null) {
            if (d == prefix.length()) return true;
            char c = prefix.charAt(d++);
            trav = trav.next[c-'A'];
        }
        return false;
    }

    public void put(String key) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (root == null) root = new Node();
        Node trav = root;
        int d = 0;
        while (trav != null) {
            if (d == key.length()) {
                trav.present = true;
                return;
            }
            char c = key.charAt(d);
            if (trav.next[c-'A'] == null) {
                trav.next[c-'A'] = new Node();
            }
            trav = trav.next[c-'A'];
            d++;
        }
    }
}
