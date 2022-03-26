/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FastCollinearPoints {

    private LineSegment[] lineSegments;

    private int segmentCount;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        checkInput(points);

        Point[] pointSorted = points.clone();
        Arrays.sort(pointSorted);
        this.lineSegments = new LineSegment[1];
        this.segmentCount = 0;

        LinkedList<Double> slopes = new LinkedList<>();
        final List<LineSegment> maxLineSegments = new LinkedList<>();

        int numPoints = points.length;

        for (int i = 0; i < numPoints; i++) {
            Point point = pointSorted[i];
            Point[] pointsCheck = pointSorted.clone();
            // Compare all points with current point
            // Sort using comparator
            // Current point is origin, we sort array according to slope of point wrt point
            Arrays.sort(pointsCheck, point.slopeOrder());

            for (int j = 1; j < numPoints; ) {
                LinkedList<Point> candidates = new LinkedList<>();
                final double REFERENCE_SLOPE = point.slopeTo(pointsCheck[j]);

                // Keep adding candidates while the slope remains the same
                do {
                    candidates.add(pointsCheck[j++]);
                    // Same slope
                } while (j < numPoints && point.slopeTo(pointsCheck[j]) == REFERENCE_SLOPE);

                // Candidates have a max line segment if ...
                // 1. Candidates are collinear: At least 4 points are located
                //    at the same line, so at least 3 slopes should be the same
                // 2. The max line segment is created by the "point" and the
                //    largest point so point should be smallest element
                //    We check this by ensuring that point is smaller than the
                //    first element as the points have already been sorted
                //    (assuming stable sort)
                if (candidates.size() >= 3
                        && point.compareTo(candidates.getFirst()) < 0) {
                    addLineSegment(new LineSegment(point, candidates.getLast()));
                }
            }
        }
        resize(this.segmentCount);
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.segmentCount;
    }

    // the line segments
    public LineSegment[] segments() {
        return this.lineSegments.clone();
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println("--------------------");
        StdOut.println("Results");
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
