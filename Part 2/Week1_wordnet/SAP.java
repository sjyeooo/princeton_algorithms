/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        check(v, w);

        int[] result = returnBFS(v, w);

        return result[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        check(v, w);

        int[] result = returnBFS(v, w);

        return result[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    // Recall that shortest length is found using breath first search
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        check(v, w);

        if (numIterableElements(v) == 0 || numIterableElements(w) == 0)
            return -1;

        int[] result = returnBFS(v, w);

        return result[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        check(v, w);

        if (numIterableElements(v) == 0 || numIterableElements(w) == 0)
            return -1;

        int[] result = returnBFS(v, w);

        return result[1];
    }

    private int numIterableElements(Iterable<Integer> v) {
        int numElements = 0;
        for (Integer e : v) {
            numElements++;
        }
        return numElements;
    }

    // Private check function for integers
    private void check(int v, int w) {
        if (v < 0 || w < 0 || v > G.V() - 1 || w > G.V() - 1) {
            throw new IllegalArgumentException("not in range");
        }
    }

    // Private check function for iterable elements
    private void check(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Integer val : v) {
            if (val == null) {
                throw new IllegalArgumentException("One of the elements inside interable is null");
            }
        }
        for (Integer val : w) {
            if (val == null) {
                throw new IllegalArgumentException("One of the elements inside interable is null");
            }
        }
    }

    // Return bfs instance
    private int[] returnBFS(int v, int w) {
        return bfs(new BreadthFirstDirectedPaths(this.G, v),
                   new BreadthFirstDirectedPaths(this.G, w));
    }

    private int[] returnBFS(Iterable<Integer> v, Iterable<Integer> w) {
        return bfs(new BreadthFirstDirectedPaths(this.G, v),
                   new BreadthFirstDirectedPaths(this.G, w));
    }

    // Accept input of BFS type and operate using the bfs object
    private int[] bfs(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int minLength = Integer.MAX_VALUE;
        int ancestor = -1;

        // Iterate through all elements of i
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int distance = bfsV.distTo(i) + bfsW.distTo(i);
                if (distance < minLength) {
                    minLength = distance;
                    ancestor = i;
                }
            }
        }

        if (minLength == Integer.MAX_VALUE) {
            minLength = -1;
        }

        int[] ret = { minLength, ancestor };

        return ret;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
