/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {
    private final Set<Point2D> points = new TreeSet<>();

    // construct an empty set of points
    public PointSET() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        points.forEach(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        List<Point2D> pointsInsideRect = new LinkedList<>();
        for (Point2D indivPoint : points) {
            if (rect.contains(indivPoint)) {
                pointsInsideRect.add(indivPoint);
            }
        }
        return pointsInsideRect;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        // Handle input null
        if (p == null)
            throw new IllegalArgumentException();

        // Handle case where points is empty
        // There is no points to compare against p
        if (isEmpty())
            return null;

        double nearestPointDist = Double.MAX_VALUE;
        // Initialise the nearest point as first point of the iterator
        Point2D nearestPoint = points.iterator().next();
        // Go through set of points to check for closest distance
        for (Point2D currPointCheck : points) {
            double currDist = currPointCheck.distanceTo(p);
            if (currDist < nearestPointDist) {
                nearestPoint = currPointCheck;
                nearestPointDist = currDist;
            }
        }
        return nearestPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // No unit tests
    }
}
