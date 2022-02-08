/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solver {

    private final Deque<Board> solutionBoards;
    private boolean isSolvable;
    private int numMoves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();

        SearchNode solutionNode = null;

        this.isSolvable = false;
        this.solutionBoards = new ArrayDeque<>();
        this.numMoves = 0;
        MinPQ<SearchNode> minPQ = new MinPQ<>();

        minPQ.insert(new SearchNode(initial, null));

        // Build tree of search nodes
        while (true) {
            // Handle current
            SearchNode currNode = minPQ.delMin();
            // Solved board
            if (currNode.board.isGoal()) {
                solutionNode = currNode;
                this.isSolvable = true;
                break;
            }
            if (currNode.board.hamming() == 2 && currNode.board.twin().isGoal()) {
                this.isSolvable = false;
                break;
            }

            // Cache the current and previous board for use in loop
            Board currBoard = currNode.board;
            Board prevBoard = currNode.past() == null ? null : currNode.past().board;
            for (Board newBoard : currBoard.neighbors()) {
                if (currNode.past() == null || // First Search Node - insert all neighbours
                        // Subsequent search node should not contain previous board
                        !newBoard.equals(prevBoard)) {
                    minPQ.insert(new SearchNode(newBoard, currNode));
                }
            }
        }

        // Solution exists
        if (isSolvable()) {
            // No moves needed
            if (minPQ.isEmpty()) {
                solutionBoards.addFirst(initial);
            }
            // Non-zero moves needed
            else {
                // Extract solution from tree
                SearchNode current = solutionNode;
                while (current.past() != null) {
                    solutionBoards.addFirst(current.board);
                    current = current.past();
                    this.numMoves++;
                }
                solutionBoards.addFirst(current.board);
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return this.isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable()) {
            return numMoves;
        }
        else {
            return -1;
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (isSolvable()) {
            return this.solutionBoards;
        }
        else {
            return null;
        }
    }

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final SearchNode past;
        private final int manhattan;
        private int moves;
        private int priority;

        // Constructor for SearchNode
        private SearchNode(Board board, SearchNode past) {
            this.board = board;
            this.past = past;
            this.manhattan = board.manhattan();
            if (past == null) {
                moves = 0;
            }
            else {
                this.moves = past.moves() + 1;
            }
            this.priority = this.manhattan + this.moves;
        }

        @Override
        public int compareTo(SearchNode that) {
            return (this.priority() - that.priority());
        }

        private int priority() {
            return (this.priority);
        }

        public int moves() {
            return this.moves;
        }

        public SearchNode past() {
            return this.past;
        }
    }


    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
