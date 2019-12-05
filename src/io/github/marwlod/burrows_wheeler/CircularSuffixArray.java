package io.github.marwlod.burrows_wheeler;

import java.util.Arrays;

public class CircularSuffixArray {
    private final int length;
    private final Integer[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("Argument cannot be null");
        length = s.length();
        suffixes = new Integer[length];
        for (int i = 0; i < length; i++) {
            suffixes[i] = i;
        }

        // TODO could be improved using 3-way radix quicksort
        Arrays.sort(suffixes, (first, second) -> {
            for (int i = 0; i < length; i++) {
                int thisChar = s.charAt(first++ % length);
                int thatChar = s.charAt(second++ % length);
                if (thisChar != thatChar) {
                    return Integer.compare(thisChar, thatChar);
                }
            }
            return 0;
        });
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length-1) throw new IllegalArgumentException("Index out of range");
        return suffixes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String tested = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(tested);
        for (int i = 0; i < tested.length(); i++) {
            System.out.print(csa.index(i) + ", ");
        }
        System.out.println();
    }
}
