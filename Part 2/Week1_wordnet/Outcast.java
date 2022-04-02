/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wn;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        this.wn = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }
        int maxDist = Integer.MIN_VALUE;
        String noun = "";

        for (int i = 0; i < nouns.length; i++) {
            int currDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    currDist += this.wn.distance(nouns[i], nouns[j]);
                }
            }
            if (currDist > maxDist) {
                maxDist = currDist;
                noun = nouns[i];
            }
        }

        return noun;
    }

    // do unit testing of this class
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
