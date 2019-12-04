package io.github.marwlod.burrows_wheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

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
        final int[] extAscii = new int[EXT_ASCII_SIZE];
        final char[] encoded = in.toCharArray();
        final char[] sorted = in.toCharArray();
        Arrays.sort(sorted);
        int[] next = new int[in.length()];
        for (int i = 0; i < in.length(); i++) {
            int charFirstIndex = firstIndexOf(sorted, encoded[i]);
            char currChar = sorted[charFirstIndex];
            if (charFirstIndex + extAscii[currChar] == i) {
                next[(charFirstIndex + extAscii[currChar] + 1) % in.length()] = i;
            } else {
                next[charFirstIndex + extAscii[currChar]] = i;
            }
            extAscii[encoded[i]]++;
        }

        int count = 0;
        for (int i = originalIndex; count < in.length(); i = next[i]) {
            BinaryStdOut.write(sorted[i]);
            count++;
        }
        BinaryStdOut.flush();
    }

    private static int firstIndexOf(char[] chars, char key) {
        int lo = 0;
        int hi = chars.length - 1;
        while (lo <= hi) {
            // key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if (key == chars[mid] && (mid == 0 || key > chars[mid-1])) return mid;
            else if (key > chars[mid]) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
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
