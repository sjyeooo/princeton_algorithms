/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    // confidence level at 95%
    private static final double CFD_CRITICAL_VAL = 1.96;

    // Store results
    private final double[] results;

    // Store number of trials
    private final int num_trials;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException(
                    "arguments n and trials should be greater than 0");
        }

        this.results = new double[trials];
        this.num_trials = trials;

        for (int i = 0; i < trials; i++) {
            Percolation testPercolation = new Percolation(n);
            int numberOfOpenedCells = 0;
            // Keep opening until percolates
            while (!testPercolation.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);

                if (testPercolation.isOpen(row, col))
                    continue;

                testPercolation.open(row, col);
                numberOfOpenedCells++;
            }
            this.results[i] = (double) (numberOfOpenedCells) / (double) (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return (mean() - PercolationStats.CFD_CRITICAL_VAL * (stddev() / Math
                .sqrt(this.num_trials)));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return (mean() + PercolationStats.CFD_CRITICAL_VAL * (stddev() / Math
                .sqrt(this.num_trials)));
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length == 2) {
            int n = Integer.parseInt(args[0]);
            int trials = Integer.parseInt(args[1]);

            try {
                PercolationStats statsOne = new PercolationStats(n, trials);
                String confidence = statsOne.confidenceLo() + ", " + statsOne.confidenceHi();
                StdOut.println("mean                    = " + statsOne.mean());
                StdOut.println("stddev                  = " + statsOne.stddev());
                StdOut.println("95% confidence interval = [" + confidence + "]");
            }
            catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
