package io.github.marwlod.burrows_wheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.HashMap;
import java.util.Map;


public class MoveToFront {
    private static final int EXT_ASCII_SIZE = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] asciiTable = new int[EXT_ASCII_SIZE];
        for (int i = 0; i < asciiTable.length; i++) {
            asciiTable[i] = i;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < asciiTable.length; i++) {
            map.put(i, i);
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(map.get((int) c), 8);
            for (int i = map.get((int) c); i > 0; i--) {
                swap(asciiTable, i, i-1);
                swap(map, asciiTable[i], asciiTable[i-1]);
            }
            map.put((int) c, 0);
        }
        BinaryStdOut.flush();
    }

    private static void swap(Map<Integer, Integer> map, int i, int j) {
        int temp = map.get(i);
        map.put(i, map.get(j));
        map.put(j, temp);
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] asciiTable = new int[EXT_ASCII_SIZE];
        for (int i = 0; i < asciiTable.length; i++) {
            asciiTable[i] = i;
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
