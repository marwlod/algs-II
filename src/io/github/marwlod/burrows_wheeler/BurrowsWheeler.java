package io.github.marwlod.burrows_wheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int EXT_ASCII_SIZE = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        final String in = BinaryStdIn.readString();
        final CircularSuffixArray csa = new CircularSuffixArray(in);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < csa.length(); i++) {
            // index of last character of i-th original suffix
            final int a = (csa.index(i) + csa.length() - 1) % csa.length();
            // last character in i-th sorted suffix
            BinaryStdOut.write(in.charAt(a));
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        final int originalIndex = BinaryStdIn.readInt();
        final String in = BinaryStdIn.readString();
        final char[] encoded = in.toCharArray();
        final int[] counts = calculateCounts(encoded);
        int[] next = new int[in.length()];

        for (int i = 0; i < in.length(); i++) {
            char currChar = encoded[i];
            int charIndex = counts[currChar]++;
            if (charIndex == i) {
                next[(charIndex + 1) % in.length()] = i;
            } else {
                next[charIndex] = i;
            }
        }

        int count = 0;
        for (int i = next[originalIndex]; count < in.length(); i = next[i]) {
            BinaryStdOut.write(encoded[i]);
            count++;
        }
        BinaryStdOut.flush();
    }

    private static int[] calculateCounts(char[] arr) {
        int[] count = new int[EXT_ASCII_SIZE + 1];
        for (char c : arr) {
            count[c + 1]++;
        }
        for (int r = 0; r < EXT_ASCII_SIZE; r++) {
            count[r + 1] += count[r];
        }
        return count;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String symbol = args[0];
        if ("-".equals(symbol)) {
            BurrowsWheeler.transform();
        } else if ("+".equals(symbol)) {
            BurrowsWheeler.inverseTransform();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
