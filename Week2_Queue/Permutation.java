/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Write a client program WeekTwo.Permutation.java that takes an integer k as a command-line
 * argument; reads a sequence of strings from standard input using StdIn.readString(); and prints
 * exactly k of them, uniformly at random. Print each item from the sequence at most once.
 */

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);

        RandomizedQueue<String> myRandomizedQueue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty())
            myRandomizedQueue.enqueue(StdIn.readString());
        for (int i = 0; i < k; ++i)
            StdOut.println(myRandomizedQueue.dequeue());
    }
}
