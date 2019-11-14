package io.github.marwlod.word_net;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class WordNet {
    private final Map<String, Set<Integer>> nounToSynsetIds;
    private final String[] synsets;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsetsFile, String hypernymsFile) {
        if (synsetsFile == null || hypernymsFile == null) throw new IllegalArgumentException("Filenames must not be null");
        In synsetsIn = new In(synsetsFile);
        List<String> synsetList = new ArrayList<>();
        String synsetLine;
        int synsetsLen = 0;
        nounToSynsetIds = new TreeMap<>();

        // e.g. line "36,AND_circuit AND_gate" means synset with ID 36 has two synonyms inside: AND_circuit and AND_gate
        while ((synsetLine = synsetsIn.readLine()) != null) {
            String[] words = synsetLine.split(",");
            int synsetId = Integer.parseInt(words[0]);
            synsetList.add(words[1]);
            String[] syns = words[1].split(" ");
            for (String syn : syns) {
                Set<Integer> synsetIds = nounToSynsetIds.computeIfAbsent(syn, k -> new TreeSet<>());
                synsetIds.add(synsetId);
            }
            synsetsLen++;
        }
        synsets = synsetList.toArray(new String[0]);

        In hypernymsIn = new In(hypernymsFile);
        Digraph digraph = new Digraph(synsetsLen);
        String hypernymLine;
        boolean rooted = false;

        // e.g. line "42,165,288" means that synset with ID 42 has two hypernyms: 165 and 288
        while ((hypernymLine = hypernymsIn.readLine()) != null) {
            String[] words = hypernymLine.split(",");
            // means we found a sink (or vertex with 0 indegree and outdegree)
            if (words.length == 1) rooted = true;
            for (int i = 1; i < words.length; i++) {
                digraph.addEdge(Integer.parseInt(words[0]), Integer.parseInt(words[i]));
            }
        }
        if (!rooted) throw new IllegalArgumentException("Graph created from files must be rooted");
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        if (directedCycle.hasCycle()) throw new IllegalArgumentException("Graph must not have any cycles");
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToSynsetIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Word must not be null");
        return nounToSynsetIds.get(word) != null;
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Nouns must not be null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Strings must be valid WordNet nouns");
        return sap.length(nounToSynsetIds.get(nounA), nounToSynsetIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Nouns must not be null");
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("Strings must be valid WordNet nouns");
        int ancestor = sap.ancestor(nounToSynsetIds.get(nounA), nounToSynsetIds.get(nounB));
        return synsets[ancestor];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        String nounA = "1820_s";
        String nounB = "1840";
        int dist = wordNet.distance(nounA, nounB);
        String sap = wordNet.sap(nounA, nounB);
        System.out.println("Dist: " + dist + ", sap: " + sap);
        System.out.println(wordNet.isNoun("401-k"));
    }
}
