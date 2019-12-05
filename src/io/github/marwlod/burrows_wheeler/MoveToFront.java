package io.github.marwlod.burrows_wheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int EXT_ASCII_SIZE = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] asciiTable = new char[EXT_ASCII_SIZE];
        for (int i = 0; i < asciiTable.length; i++) {
            asciiTable[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int cIndex = 0;
            while (asciiTable[cIndex] != c) cIndex++;
            BinaryStdOut.write(cIndex, 8);
            for (int i = cIndex; i > 0; i--) {
                swap(asciiTable, i, i-1);
            }
        }
        BinaryStdOut.flush();
    }

    private static void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] asciiTable = new char[EXT_ASCII_SIZE];
        for (int i = 0; i < asciiTable.length; i++) {
            asciiTable[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(asciiTable[c], 8);
            for (int i = c; i > 0; i--) {
                swap(asciiTable, i, i-1);
            }
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String symbol = args[0];
        if ("-".equals(symbol)) {
            MoveToFront.encode();
        } else if ("+".equals(symbol)) {
            MoveToFront.decode();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
