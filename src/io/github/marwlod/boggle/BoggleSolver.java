package io.github.marwlod.boggle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private final RWayTrie dictionary;

    private static class Point {
        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class BagNode {
        private Point val;
        private BagNode next;

        private BagNode(Point val, BagNode next) {
            this.val = val;
            this.next = next;
        }
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new RWayTrie();
        for (String word : dictionary) {
            this.dictionary.put(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final Set<String> validWords = new HashSet<>();
        final BagNode[][] adjMatrix = new BagNode[board.cols()][board.rows()];
        for (int x = 0; x < board.cols(); x++) {
            for (int y = 0; y < board.rows(); y++) {
                adjMatrix[x][y] = adj(board, x, y);
            }
        }
        for (int x = 0; x < board.cols(); x++) {
            for (int y = 0; y < board.rows(); y++) {
                boolean[][] marked = new boolean[board.cols()][board.rows()];
                String startingString = board.getLetter(y, x) == 'Q' ? "QU" : String.valueOf(board.getLetter(y, x));
                addValidWords(adjMatrix, marked, startingString, validWords, board, x, y);
            }
        }
        return validWords;
    }

    private void addValidWords(BagNode[][] adjMatrix, boolean[][] marked, String currWord, Set<String> validWords, BoggleBoard board, int x, int y) {
        if (currWord.length() > 2 && dictionary.contains(currWord)) {
            validWords.add(currWord);
        }
        marked[x][y] = true;
        for (BagNode trav = adjMatrix[x][y]; trav.next != null; trav = trav.next) {
            if (!marked[trav.val.x][trav.val.y]) {
                final String nextWord = currWord + (board.getLetter(trav.val.y, trav.val.x) == 'Q' ? "QU" : board.getLetter(trav.val.y, trav.val.x));
                if (dictionary.hasWordsWithPrefix(nextWord)) {
                    addValidWords(adjMatrix, marked, nextWord, validWords, board, trav.val.x, trav.val.y);
                    marked[trav.val.x][trav.val.y] = false;
                }
            }
        }
    }

    private BagNode adj(BoggleBoard board, int x, int y) {
        final BagNode root = new BagNode(null, null);
        BagNode trav = root;
        // up
        if (y < board.rows()-1) trav = putAndTraverse(trav, x, y+1);
        // upper right
        if (x < board.cols()-1 && y < board.rows()-1) trav = putAndTraverse(trav, x+1, y+1);
        // right
        if (x < board.cols()-1) trav = putAndTraverse(trav, x+1, y);
        // down right
        if (x < board.cols()-1 && y > 0) trav = putAndTraverse(trav, x+1, y-1);
        // down
        if (y > 0) trav = putAndTraverse(trav, x, y-1);
        // down left
        if (x > 0 && y > 0) trav = putAndTraverse(trav, x-1, y-1);
        // left
        if (x > 0) trav = putAndTraverse(trav, x-1, y);
        // upper left
        if (x > 0 && y < board.rows()-1) putAndTraverse(trav, x-1, y+1);
        return root;
    }

    private BagNode putAndTraverse(BagNode trav, int x, int y) {
        trav.val = new Point(x, y);
        trav.next = new BagNode(null, null);
        trav = trav.next;
        return trav;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dictionary.contains(word)) return 0;
        final int points;
        if (word.length() < 3) points = 0;
        else if (word.length() == 3 || word.length() == 4) points = 1;
        else if (word.length() == 5) points = 2;
        else if (word.length() == 6) points = 3;
        else if (word.length() == 7) points = 5;
        else points = 11;
        return points;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        long start = System.nanoTime();
            for (String word : solver.getAllValidWords(board)) {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
        long end = System.nanoTime();
        StdOut.println("Score = " + score);
        StdOut.println("Program duration: " + ((end - start)/ (1000*1000)) + "ms");
    }
}
