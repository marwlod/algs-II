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
    private int[] edgeTo;
    private int[] distFromRoot;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null || G.V() == 0) throw new IllegalArgumentException("Graph must not be null nor empty");
        this.edgeTo = new int[G.V()];
        this.distFromRoot = new int[G.V()];
        Arrays.fill(distFromRoot, -1);
        boolean[] tempMarked = new boolean[G.V()];
        // reverse the graph to convert the root from sink to source
        buildPaths(G.reverse(), getRoot(G, tempMarked, 0));
    }

    // DFS until sink (root) found
    private int getRoot(Digraph G, boolean[] marked, int v) {
        marked[v] = true;
        if (G.outdegree(v) == 0) return v;
        for (int adj : G.adj(v)) {
            if (!marked[adj]) {
                getRoot(G, marked, adj);
            }
        }
        return -1;
    }

    // BFS to build paths and saves distance from every vertex to root
    private void buildPaths(Digraph G, int root) {
        Queue<Integer> q = new LinkedList<>();
        q.add(root);
        distFromRoot[root] = 0;
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int adj : G.adj(v)) {
                // not yet checked
                if (distFromRoot[adj] == -1) {
                    distFromRoot[adj] = distFromRoot[v] + 1;
                    edgeTo[adj] = v;
                    q.add(adj);
                }
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        int len = 0;
        if (noPathBetween(v, w)) return -1;
        while (distFromRoot[v] > distFromRoot[w]) {
            v = edgeTo[v];
            len++;
        }
        while (distFromRoot[w] > distFromRoot[v]) {
            w = edgeTo[w];
            len++;
        }
        while (v != w) {
            v = edgeTo[v];
            w = edgeTo[w];
            len += 2;
        }
        return len;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if (noPathBetween(v, w)) return -1;
        while (distFromRoot[v] > distFromRoot[w]) v = edgeTo[v];
        while (distFromRoot[w] > distFromRoot[v]) w = edgeTo[w];
        while (v != w) {
            v = edgeTo[v];
            w = edgeTo[w];
        }
        return v;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> vs, Iterable<Integer> ws) {
        if (vs == null || ws == null) throw new IllegalArgumentException("Iterables must not be null");
        validateVertexIterable(vs);
        validateVertexIterable(ws);
        int shortestLen = Integer.MAX_VALUE;
        for (Integer v : vs) {
            for (Integer w : ws) {
                if (noPathBetween(v, w)) return -1;
                int len = length(v, w);
                if (len < shortestLen) {
                    shortestLen = len;
                }
            }
        }
        return shortestLen;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> vs, Iterable<Integer> ws) {
        if (vs == null || ws == null) throw new IllegalArgumentException("Iterables must not be null");
        validateVertexIterable(vs);
        validateVertexIterable(ws);
        int shortestLen = Integer.MAX_VALUE;
        int sapV = -1, sapW = -1;
        for (Integer v : vs) {
            for (Integer w : ws) {
                if (noPathBetween(v, w)) return -1;
                int len = length(v, w);
                if (len < shortestLen) {
                    shortestLen = len;
                    sapV = v;
                    sapW = w;
                }
            }
        }
        return ancestor(sapV, sapW);
    }

    private boolean noPathBetween(int v, int w) {
        return distFromRoot[v] == -1 || distFromRoot[w] == -1;
    }

    private void validateVertex(Integer v) {
        if (v == null || v < 0 || v >= edgeTo.length) throw new IllegalArgumentException("Invalid vertex number supplied");
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
