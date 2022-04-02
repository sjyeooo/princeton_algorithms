/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private final Digraph digraph;
    private final SAP sap;
    private final Map<String, List<Integer>> nouns = new HashMap<>();
    private Map<Integer, String> synsets = new HashMap<>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Argument should not be null");

        // Read in synsets file
        In in = new In(synsets);
        this.synsets = new HashMap<>();
        while (!in.isEmpty()) {
            // line[0] is synset ID
            // line[1] is the synonym set (aka synset)
            // line[2] is gloss (irrelevant)
            String[] line = in.readLine().split(",");
            int synsetID = Integer.parseInt(line[0]);
            this.synsets.put(synsetID, line[1]);
            Arrays.stream(line[1].split(" "))
                  .forEach(word -> {
                      if (!nouns.containsKey(word)) {
                          nouns.put(word, new ArrayList<>());
                      }
                      nouns.get(word).add(synsetID);
                  });
        }
        in.close();

        this.digraph = new Digraph(this.synsets.size());
        in = new In(hypernyms);
        while (!in.isEmpty()) {
            // line[0] is synsetID
            // line[1...] is hypernym of the synset ID
            String[] line = in.readLine().split(",");
            int synsetID = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                // synset --> line[i]
                digraph.addEdge(synsetID, Integer.parseInt(line[i]));
            }
        }
        in.close();
        DirectedCycle finder = new DirectedCycle(digraph);
        if (finder.hasCycle()) {
            throw new IllegalArgumentException("Graph provided is not a DAG");
        }
        if (!isRootedDAG()) {
            throw new IllegalArgumentException("Graph provided is not a rooted DAG");
        }

        this.sap = new SAP(this.digraph);
    }

    private boolean isRootedDAG() {
        int cnt = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.indegree(i) != 0 &&         // Vertex has more than one inbound
                    digraph.outdegree(i) == 0) {    //Vertex has no outbound
                cnt++;
            }
        }
        return (cnt == 1);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Noun is not found in list of synsets");
        }

        return this.sap.length(getIterable(nounA), getIterable(nounB));
    }

    private Iterable<Integer> getIterable(String noun) {
        return nouns.get(noun);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Noun is not found in list of synsets");
        }

        int ancestorID = this.sap.ancestor(getIterable(nounA), getIterable(nounB));

        //Convert from ID to synset
        return synsets.get(ancestorID);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        System.out.println(wordNet.distance("a", "c"));
        System.out.println(wordNet.sap("a", "c"));
        System.out.println(wordNet.nouns());
        System.out.println(wordNet.isNoun("d"));
        System.out.println(wordNet.isNoun("i"));
    }
}
