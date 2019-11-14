package io.github.marwlod.word_net;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String leastRelated = null;
        int maxDist = Integer.MIN_VALUE;
        for (String curr : nouns) {
            int distToOthers = 0;
            for (String other : nouns) {
                distToOthers += wordNet.distance(curr, other);
            }
            if (distToOthers > maxDist) {
                maxDist = distToOthers;
                leastRelated = curr;
            }
        }
        return leastRelated;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
