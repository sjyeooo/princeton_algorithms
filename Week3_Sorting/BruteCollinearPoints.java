/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BruteCollinearPoints {

    private LineSegment[] lineSegments;

    private final Point[] points;

    private int segmentCount;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        checkInput(points);

        this.points = points.clone();
        this.lineSegments = new LineSegment[2];
        this.segmentCount = 0;

        int numPoints = points.length;

        Arrays.sort(this.points);

        for (int i = 0; i < numPoints; i++) {
            for (int j = i + 1; j < numPoints; j++) {
                for (int k = j + 1; k < numPoints; k++) {
                    for (int l = k + 1; l < numPoints; l++) {
                        Point p = this.points[i];
                        Point q = this.points[j];
                        Point r = this.points[k];
                        Point s = this.points[l];
                        if (p.slopeTo(q) == p.slopeTo(r) &&
                                p.slopeTo(q) == p.slopeTo(s)) {
                            addLineSegment(new LineSegment(p, s));
                            p.drawTo(s);
                        }
                    }
                }
            }
        }
        resize(this.segmentCount);
    }

    private void resize(int newLength) {
        LineSegment[] newSegment = new LineSegment[newLength];

        int lowerCopyLength = 0;
        if (newLength < this.lineSegments.length)
            lowerCopyLength = newLength;
        else
            lowerCopyLength = this.lineSegments.length;

        // Copy data to new array
        for (int i = 0; i < lowerCopyLength; i++) {
            newSegment[i] = this.lineSegments[i];
        }
        this.lineSegments = newSegment;
    }

    private void addLineSegment(LineSegment lineSegment) {
        // Resize array if the array is already full and we want to add more items
        if (this.segmentCount == this.lineSegments.length) {
            resize(this.segmentCount * 2);
        }

        // Add line segment to array containing line segments
        this.lineSegments[this.segmentCount] = lineSegment;

        // Increment count of line segments
        this.segmentCount++;
    }

    private void checkInput(Point[] points) {
        // Check to ensure that points is not null
        if (points == null) {
            throw new IllegalArgumentException();
        }

        // Check to ensure that none of the inputs are not null
        for (Point p1 : points) {
            if (p1 == null) {
                throw new IllegalArgumentException();
            }
        }

        // Check that there is no duplicate points
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length && j < i; j++) {
                if (i != j && // not the same index
                        points[i].compareTo(points[j]) == 0) {  // Same point
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.segmentCount;
    }

    // the line segments
    public LineSegment[] segments() {
        return this.lineSegments.clone();
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
