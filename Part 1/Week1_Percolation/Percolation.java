/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // n-by-n grid, the n value
    private final int n;

    // grid sites
    private final WeightedQuickUnionUF sites;
    private final WeightedQuickUnionUF sitesBackwash;

    // maker array to mark open sites
    private boolean[] openedSites;

    // virtual top node identifier
    private final int virtualTopID;

    // virtual bottom node identifier
    private final int virtualBtmID;

    // number of open sites
    private int numOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }

        // initialize the system with n-by-n grid of sites, plus 2 virtual nodes: top virtual node, bottom virtual node
        // top virtual node is in position n^2
        // bottom virtual node is in position n^2 + 1
        this.sites = new WeightedQuickUnionUF(n * n + 2);
        this.sitesBackwash = new WeightedQuickUnionUF(n * n + 1);

        // Initialize n^n items
        this.openedSites = new boolean[n * n];

        for (int i = 0; i < n; i++) {
            this.openedSites[i] = false;
        }

        this.n = n;
        this.virtualTopID = n * n;
        this.virtualBtmID = n * n + 1;
        this.numOpenSites = 0;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkRowCol(row, col);

        // get the 1D coordinates
        int i = this.xyTo1D(row, col);

        if (this.n == 1) {  // Handle case where n = 1, just connect top and bottom.
            this.openedSites[0] = true;
            this.numOpenSites += 1;

            this.sites.union(0, this.virtualTopID);
            this.sites.union(0, this.virtualBtmID);

            this.sitesBackwash.union(0, this.virtualTopID);
        }
        else {
            // Avoid opened sites
            if (!this.openedSites[i]) {

                // Open site
                this.openedSites[i] = true;
                this.numOpenSites += 1;

                // Handle connection to top or bottom
                if (row == 1) {
                    this.sites.union(this.virtualTopID, i);
                    this.sitesBackwash.union(this.virtualTopID, i);
                }
                if (row == this.n) {
                    this.sites.union(this.virtualBtmID, i);
                }

                // Handle connection of site to neighbours
                tryUnion(row, col, row - 1, col);
                tryUnion(row, col, row + 1, col);
                tryUnion(row, col, row, col - 1);
                tryUnion(row, col, row, col + 1);
            }
        }
    }

    private void checkRowCol(int row, int col) {

        if ((row <= 0 || row > this.n)) {
            throw new IllegalArgumentException(
                    "row and col must be within the range 1 - " + this.n + ", " +
                            "inclusively. You entered row: " + row);
        }
        if ((col <= 0 || col > this.n)) {
            throw new IllegalArgumentException(
                    "row and col must be within the range 1 - " + this.n + ", " +
                            "inclusively. You entered col: " + col);
        }
    }

    private void tryUnion(int rowA, int colA, int rowB, int colB) {
        if (0 < rowB && rowB <= n && 0 < colB && colB <= n  // Within bounds
                && isOpen(rowB, colB)) {                    // Open site
            this.sites.union(xyTo1D(rowA, colA), xyTo1D(rowB, colB));
            this.sitesBackwash.union(xyTo1D(rowA, colA), xyTo1D(rowB, colB));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);

        // get the 1D coordinates
        int i = this.xyTo1D(row, col);

        return (this.openedSites[i]);
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkRowCol(row, col);

        // get the 1D coordinates
        int i = this.xyTo1D(row, col);

        return (this.sitesBackwash.find(this.virtualTopID) == this.sitesBackwash.find(i) &&
                isOpen(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return (this.sites.find(this.virtualTopID) == this.sites.find(this.virtualBtmID));
    }

    // test client (optional)
    public static void main(String[] args) {
        // Intentionally empty
    }

    /*
     * Turn 2D coordinates to 1D coordinates
     * @return int
     */
    private int xyTo1D(int row, int col) {
        // Check for bounds and throw exception if out of bounds
        if ((row <= 0 || row > this.n) || (col <= 0 || col > this.n)) {
            throw new IndexOutOfBoundsException(
                    "row and col must be within the range 1 - " + this.n + ", " +
                            "inclusively. You entererd row: " + row + ", col: " + col);
        }

        // since by convention the row and column indices are integers between 1 and n
        // we need to offset row and col by 1
        // and the formula to calculating indices in 1D is as followed: row * n + col
        return this.n * (row - 1) + (col - 1);
    }
}
