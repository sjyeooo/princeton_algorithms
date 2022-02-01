/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        int n = 0;
        String readIn;
        String chosen = "";

        while (!StdIn.isEmpty()) {
            readIn = StdIn.readString();
            n++;

            if (StdRandom.bernoulli(1.0 / (double) n))
                chosen = readIn;
        }

        StdOut.println(chosen);
    }
}
