package io.github.marwlod.word_net;

public class WordNet {
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("File names must not be null");
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return null;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Word must not be null");
        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Nouns must not be null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Strings must be valid WordNet nouns");
        return 0;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Nouns must not be null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Strings must be valid WordNet nouns");
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
