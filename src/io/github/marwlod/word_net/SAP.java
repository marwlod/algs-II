package io.github.marwlod.word_net;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SAP {
    private final Digraph dg;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph dg) {
        if (dg == null || dg.V() == 0) throw new IllegalArgumentException("Graph must not be null nor empty");
        this.dg = new Digraph(dg);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        int[] distToV = computeDists(v);
        int[] distToW = computeDists(w);
        return findSapLen(distToV, distToW);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        int[] distToV = computeDists(v);
        int[] distToW = computeDists(w);
        return findSapAncestor(distToV, distToW);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> vs, Iterable<Integer> ws) {
        if (vs == null || ws == null) throw new IllegalArgumentException("Iterables must not be null");
        validateVertexIterable(vs);
        validateVertexIterable(ws);
        int[] distToV = computeShortestDists(vs);
        int[] distToW = computeShortestDists(ws);
        return findSapLen(distToV, distToW);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> vs, Iterable<Integer> ws) {
        if (vs == null || ws == null) throw new IllegalArgumentException("Iterables must not be null");
        validateVertexIterable(vs);
        validateVertexIterable(ws);
        int[] distToV = computeShortestDists(vs);
        int[] distToW = computeShortestDists(ws);
        return findSapAncestor(distToV, distToW);
    }

    private int findSapLen(int[] distToV, int[] distToW) {
        int shortestLen = Integer.MAX_VALUE;
        for (int i = 0; i < dg.V(); i++) {
            int currLen = (distToV[i] == Integer.MAX_VALUE || distToW[i] == Integer.MAX_VALUE)
                    ? Integer.MAX_VALUE : distToV[i] + distToW[i];
            if (currLen < shortestLen) {
                shortestLen = currLen;
            }
        }
        if (shortestLen == Integer.MAX_VALUE) return -1;
        return shortestLen;
    }

    private int findSapAncestor(int[] distToV, int[] distToW) {
        int shortestLen = Integer.MAX_VALUE;
        int ancestor = Integer.MAX_VALUE;
        for (int i = 0; i < dg.V(); i++) {
            int currLen = (distToV[i] == Integer.MAX_VALUE || distToW[i] == Integer.MAX_VALUE)
                    ? Integer.MAX_VALUE : distToV[i] + distToW[i];
            if (currLen < shortestLen) {
                shortestLen = currLen;
                ancestor = i;
            }
        }
        if (ancestor == Integer.MAX_VALUE) return -1;
        return ancestor;
    }

    private int[] computeDists(int source) {
        int[] distTo = new int[dg.V()];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        distTo[source] = 0;
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int adj : dg.adj(v)) {
                if (distTo[adj] == Integer.MAX_VALUE) {
                    distTo[adj] = distTo[v] + 1;
                    q.add(adj);
                }
            }
        }
        return distTo;
    }

    private int[] computeShortestDists(Iterable<Integer> source) {
        int[] distTo = new int[dg.V()];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        Queue<Integer> q = new LinkedList<>();
        for (int s : source) {
            q.add(s);
            distTo[s] = 0;
        }
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int adj : dg.adj(v)) {
                if (distTo[adj] == Integer.MAX_VALUE) {
                    distTo[adj] = distTo[v] + 1;
                    q.add(adj);
                }
            }
        }
        return distTo;
    }

    private void validateVertex(Integer v) {
        if (v == null || v < 0 || v >= dg.V()) throw new IllegalArgumentException("Invalid vertex number supplied");
    }

    private void validateVertexIterable(Iterable<Integer> vs) {
        for (Integer v : vs) {
            validateVertex(v);
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(new Digraph(G));
        Iterable<Integer> vs = new ArrayList<>(Arrays.asList(13, 23, 24));
        Iterable<Integer> ws = new ArrayList<>(Arrays.asList(6, 16, 17));
        int length   = sap.length(vs, ws);
        int ancestor = sap.ancestor(vs, ws);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            length   = sap.length(v, w);
            ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
