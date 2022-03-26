/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class Board {

    private final int[][] blocks;
    private final int N;
    private int blankRow;
    private int blankCol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] blocks) {

        if (blocks == null) {
            throw new NullPointerException();
        }

        this.blocks = copyOf(blocks);
        N = blocks.length;
        blankRow = -1;
        blankCol = -1;

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (blocks[row][col] == 0) {
                    blankRow = row;
                    blankCol = col;
                    return;
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.N);
        stringBuilder.append("\n");
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                stringBuilder.append(String.format("%2d ", this.blocks[row][col]));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    // board dimension n
    public int dimension() {
        return this.N;
    }

    // number of tiles out of place
    public int hamming() {
        int numOutOfPlace = 0;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (blankRow == row && blankCol == col) {
                    continue;
                }
                // If tile is out of place
                if (manhattan(row, col) != 0) {
                    numOutOfPlace++;
                }
            }
        }
        return numOutOfPlace;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int result = 0;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (row == blankRow && col == blankCol)
                    continue;
                result += manhattan(row, col);
            }
        }
        return result;
    }

    // Compute manhattan distance of each tile
    // Note that row and col is [0, N-1]
    private int manhattan(int row, int col) {
        int num = this.blocks[row][col] - 1;
        int desRow = num / this.N;
        int desCol = num % this.N;

        // Return distance from desired row and column
        return Math.abs(desRow - row) + Math.abs(desCol - col);
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (hamming() == 0 || manhattan() == 0)
            return true;
        else
            return false;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // If input is null
        if (y == null)
            return false;

        // If reference is the same
        if (y == this)
            return true;

        // If input is not the same class type
        if (this.getClass() != y.getClass())
            return false;

        // Check elements
        Board that = (Board) y;
        if (this.blankCol != that.blankCol)
            return false;
        if (this.blankRow != that.blankRow)
            return false;
        if (this.N != that.N)
            return false;
        return (Arrays.deepEquals(this.blocks, that.blocks));
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        LinkedList<Board> neighbours = new LinkedList<>();
        if (checkValid(this.blankRow - 1, this.blankCol))
            neighbours.push(new Board(swap(this.blocks, this.blankRow, this.blankCol,
                                           this.blankRow - 1, this.blankCol)));
        if (checkValid(this.blankRow + 1, this.blankCol))
            neighbours.push(new Board(swap(this.blocks, this.blankRow, this.blankCol,
                                           this.blankRow + 1, this.blankCol)));
        if (checkValid(this.blankRow, this.blankCol - 1))
            neighbours.push(new Board(swap(this.blocks, this.blankRow, this.blankCol,
                                           this.blankRow, this.blankCol - 1)));
        if (checkValid(this.blankRow, this.blankCol + 1))
            neighbours.push(new Board(swap(this.blocks, this.blankRow, this.blankCol,
                                           this.blankRow, this.blankCol + 1)));

        return neighbours;
    }

    private int[][] swap(int[][] originalBoard, int ogRow, int ogCol, int newRow, int newCol) {
        int[][] newBoard = copyOf(originalBoard);
        int tempVal = originalBoard[newRow][newCol];
        newBoard[newRow][newCol] = originalBoard[ogRow][ogCol];
        newBoard[ogRow][ogCol] = tempVal;

        return newBoard;
    }

    // Returns true if row and col are within bounds
    private boolean checkValid(int row, int col) {
        if (row >= 0 && row < this.N && col >= 0 && col < this.N)
            return true;
        return false;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinBoard = copyOf(this.blocks);
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                // If blank row is not first, we can just (0,0) with (0,1)
                if (blankRow != 0) {
                    twinBoard = swap(this.blocks, 0, 0, 0, 1);
                }
                // If black row is first, then we just swap the next row, i.e.
                // (1,0) with (1,1)
                else {
                    twinBoard = swap(this.blocks, 1, 0, 1, 1);
                }
            }
        }
        return new Board(twinBoard);
    }

    private int[][] copyOf(int[][] matrix) {
        int[][] clone = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            clone[row] = matrix[row].clone();
        }
        return clone;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] boardARaw = { { 1, 2, 3 }, { 4, 5, 6 }, { 0, 7, 8 } };
        int[][] boardBRaw = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };

        Board boardA = new Board(boardARaw);
        Board boardACopy = new Board(boardARaw);
        Board boardB = new Board(boardBRaw);

        // Check equals
        assert (boardA.equals(boardACopy));
        assert (!boardA.equals(boardB));
        assert (!boardA.equals(null));

        // Check hamming
        assert (boardA.hamming() == 2);
        assert (boardB.hamming() == 0);

        // Check public manhattan
        assert (boardA.manhattan() == 2);
        assert (boardB.manhattan() == 0);

        // Check private manhattan
        assert (boardA.manhattan(2, 1) == 1);
        assert (boardA.manhattan(2, 2) == 1);
        assert (boardB.manhattan(2, 0) == 0);

        // Check dimension method
        assert (boardA.dimension() == 3);
        assert (boardB.dimension() == 3);

        // Check isGoal
        assert (!boardA.isGoal());
        assert (boardB.isGoal());

        int[][] unsolvable = { { 1, 2, 3 }, { 4, 5, 6 }, { 8, 7, 0 } };
        Board boardUnsolvable = new Board(unsolvable);
        StdOut.println(boardUnsolvable.twin());

        StdOut.println("Unit testing complete");
    }

}
