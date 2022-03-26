/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
import java.util.List;

public class KdTree {

    private enum DividerType {HORIZONTAL, VERTICAL}

    private Node root;
    private int size = 0;

    // Configure the grid
    private final RectHV GRID = new RectHV(0, 0, 1, 1);

    private static class Node {
        private final Point2D data;
        private Node left;
        private Node right;
        private final DividerType divType;

        Node(Point2D p, DividerType divType, Node left, Node right) {
            this.data = p;
            this.left = left;   // LEFT | BELOW
            this.right = right; // RIGHT | ABOVE
            this.divType = divType;
        }

        public DividerType nextDivider() {
            return (this.divType == DividerType.HORIZONTAL ?
                    DividerType.VERTICAL :
                    DividerType.HORIZONTAL);
        }

        public int compareTo(Point2D p) {

            // Same point, return 0
            if (this.data.x() == p.x() &&
                    this.data.y() == p.y()) {
                return 0;
            }

            // Check left or right node
            // Return -1 if point should go left node
            // Return 1 if point should go right node
            if (this.divType == DividerType.VERTICAL) {   // Horizontal
                if (p.x() < this.data.x()) // point is left of x
                    return -1;
                else
                    return 1;
            }
            else {  // Vertical
                if (p.y() < this.data.y())
                    return -1;
                else
                    return 1;
            }

        }

        // Duplicate function to compareTo(Point2D p) due to complaints from
        // Coursera checker about calls to Point2D
        public int compareTo(double x, double y) {

            // Same point, return 0
            if (this.data.x() == x &&
                    this.data.y() == y) {
                return 0;
            }

            // Check left or right node
            // Return -1 if point should go left node
            // Return 1 if point should go right node
            if (this.divType == DividerType.VERTICAL) {   // Horizontal
                if (x < this.data.x()) // point is left of x
                    return -1;
                else
                    return 1;
            }
            else {  // Vertical
                if (y < this.data.y())
                    return -1;
                else
                    return 1;
            }
        }
    }

    // construct an empty set of points
    public KdTree() {
        this.root = null;
        this.size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.size == 0;
    }

    // number of points in the set
    public int size() {
        return this.size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {

        if (p == null)
            throw new IllegalArgumentException();

        this.root = insert(this.root, p, DividerType.VERTICAL);
    }

    private Node insert(Node node, Point2D p, DividerType dividerType) {
        // Reached a node that is null - generate a new node for insert
        if (node == null) {
            this.size++;
            return new Node(p, dividerType, null, null);
        }

        // Traverse through tree to find the node
        int cmp = node.compareTo(p);
        DividerType divType = dividerType == DividerType.VERTICAL ?
                              DividerType.HORIZONTAL :
                              DividerType.VERTICAL;
        if (cmp < 0)
            node.left = insert(node.left, p, divType);
        else if (cmp > 0)
            node.right = insert(node.right, p, divType);

        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (isEmpty())
            return false;

        Node node = this.root;

        while (node != null) {
            int cmp = node.compareTo(p);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else /* if (cmp == 0) */ return true;
        }
        return false;
    }

    // A 2d-tree divides the unit square in a simple way: all the points to the
    // left of the root go in the left subtree; all those to the right go in the
    // right subtree; and so forth, recursively. Your draw() method should draw
    // all of the points to standard draw in black and the subdivisions in red
    // (for vertical splits) and blue (for horizontal splits). This method need
    // not be efficient—it is primarily for debugging.
    public void draw() {
        // Configuration for drawing
        StdDraw.setScale(0, 1);

        draw(this.root, this.GRID);
    }

    private void draw(Node node, RectHV rect) {
        // Stop drawing when we reach a null node (end of the tree)
        if (node == null)
            return;

        // Draw point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.05);
        StdDraw.point(node.data.x(), node.data.y());

        // Draw lines
        StdDraw.setPenRadius(0.01);
        if (node.divType == DividerType.VERTICAL) { // Vertical divider --> draw vertical lines
            StdDraw.setPenColor(StdDraw.RED);
            // Draw current vertical line
            StdDraw.line(node.data.x(), rect.ymin(), node.data.x(), rect.ymax());
            // command the drawing of the next horizontal lines
            // Left bounded by xmin and current node's x
            draw(node.left, new RectHV(rect.xmin(), rect.ymin(),
                                       node.data.x(), rect.ymax()));
            // Right bounded by current node's x and xmax
            draw(node.right, new RectHV(node.data.x(), rect.ymin(),
                                        rect.xmax(), rect.ymax()));
        }
        else { // Horizontal divider --> draw horizontal lines
            StdDraw.setPenColor(StdDraw.BLUE);
            // Draw current horizontal line
            StdDraw.line(rect.xmin(), node.data.y(), rect.xmax(), node.data.y());
            // command the drawing of the next horizontal lines
            // Bottom bounded by ymin and current node's y
            draw(node.left, new RectHV(rect.xmin(), rect.ymin(),
                                       rect.xmax(), node.data.y()));
            // Top bounded by current node's y and ymax
            draw(node.right, new RectHV(rect.xmin(), node.data.y(),
                                        rect.xmax(), rect.ymax()));
        }
    }

    // To find all points contained in a given query rectangle, start at the
    // root and recursively search for points in both subtrees using the
    // following pruning rule: if the query rectangle does not intersect the
    // rectangle corresponding to a node, there is no need to explore that node
    // (or its subtrees). A subtree is searched only if it might contain a point
    // contained in the query rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        List<Point2D> pointsInRange = new LinkedList<>();

        range(this.root, rect, pointsInRange);

        return pointsInRange;
    }

    private void range(Node node, RectHV rect, List<Point2D> pointsInRange) {
        // Reached end of tree
        if (node == null) {
            return;
        }

        // Point lies within the rectangle
        // --> Need to search both branches
        if (rect.contains(node.data)) {
            // Add current point to list of points in range
            pointsInRange.add(node.data);
            range(node.left, rect, pointsInRange);
            range(node.right, rect, pointsInRange);
            return;
        }

        // Rectangle bottom left corner is above/right of point
        // --> go to left/bottom node
        if (node.compareTo(rect.xmin(), rect.ymin()) < 0) {
            range(node.left, rect, pointsInRange);
        }
        // Rectangle top right corner is below/left of point
        // --> go to right/top node
        if (node.compareTo(rect.xmax(), rect.ymax()) > 0) {
            range(node.right, rect, pointsInRange);
        }
    }

    // Nearest-neighbor search. To find a closest point to a given query point,
    // start at the root and recursively search in both subtrees using the
    // following pruning rule: if the closest point discovered so far is closer
    // than the distance between the query point and the rectangle corresponding
    // to a node, there is no need to explore that node (or its subtrees). That
    // is, search a node only only if it might contain a point that is closer
    // than the best one found so far. The effectiveness of the pruning rule
    // depends on quickly finding a nearby point. To do this, organize the
    // recursive method so that when there are two possible subtrees to go down,
    // you always choose the subtree that is on the same side of the splitting
    // line as the query point as the first subtree to explore—the closest point
    // found while exploring the first subtree may enable pruning of the second
    // subtree.
    //
    // Search through KdTree to find the point inside KdTree that is nearest to
    // point p
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (isEmpty()) {
            return null;
        }

        return nearest(this.root, p, this.root.data, this.GRID);

    }

    private Point2D nearest(Node node, Point2D query, Point2D closest, RectHV rect) {
        // Reached end of the search
        if (node == null)
            return closest;

        double closestDist = closest.distanceTo(query);

        // Recursively search left/bottom OR right/above if contains closer point
        if (rect.distanceTo(query) < closestDist) {
            // Update closest point if current node is closer
            if (node.data.distanceTo(query) < closestDist) {
                closest = node.data;
            }
            // Go down the tree to check for more closest points
            if (node.divType == DividerType.VERTICAL) {
                // when there are two possible subtrees to go down, you always
                // choose the subtree that is on the same side of the splitting
                // line as the query point as the first subtree to explore
                // closest point found while exploring the first subtree may
                // enable pruning of the second subtree
                if (query.x() < node.data.x()) {
                    // Go down left first as closest point is at left side
                    closest = nearest(node.left, query, closest,
                                      new RectHV(rect.xmin(), rect.ymin(),
                                                 node.data.x(), rect.ymax()));
                    closest = nearest(node.right, query, closest,
                                      new RectHV(node.data.x(), rect.ymin(),
                                                 rect.xmax(), rect.ymax()));
                }
                else {
                    // Go down right first as closest point is a the right side
                    closest = nearest(node.right, query, closest,
                                      new RectHV(node.data.x(), rect.ymin(),
                                                 rect.xmax(), rect.ymax()));
                    closest = nearest(node.left, query, closest,
                                      new RectHV(rect.xmin(), rect.ymin(),
                                                 node.data.x(), rect.ymax()));
                }
            }
            else {
                // Go down left first as closest point is at left branch (below)
                if (query.y() < node.data.y()) {
                    closest = nearest(node.left, query, closest,
                                      new RectHV(rect.xmin(), rect.ymin(),
                                                 rect.xmax(), node.data.y()));
                    closest = nearest(node.right, query, closest,
                                      new RectHV(rect.xmin(), node.data.y(),
                                                 rect.xmax(), rect.ymax()));
                }
                else {
                    // Go down right first as closest point is a the right branch (above)
                    closest = nearest(node.right, query, closest,
                                      new RectHV(rect.xmin(), node.data.y(),
                                                 rect.xmax(), rect.ymax()));
                    closest = nearest(node.left, query, closest,
                                      new RectHV(rect.xmin(), rect.ymin(),
                                                 rect.xmax(), node.data.y()));
                }
            }
        }
        return closest;
    }


    public static void main(String[] args) {
        KdTree kdTree = new KdTree();

        // Initialisation should be empty
        assert (kdTree.isEmpty());
        assert (kdTree.size() == 0);

        // Insert points over time, check and ensure that size, contains are implemented correctly
        kdTree.insert(new Point2D(0.2, 0.2));
        assert (!kdTree.isEmpty());
        assert (kdTree.size() == 1);
        assert (kdTree.contains(new Point2D(0.2, 0.2)));
        assert (!kdTree.contains(new Point2D(0.25, 0.4)));
        assert (!kdTree.contains(new Point2D(0.5, 0.5)));
        assert (!kdTree.contains(new Point2D(0.1, 0.1)));
        assert (!kdTree.contains(new Point2D(0.8, 0.4)));

        kdTree.insert(new Point2D(0.25, 0.4));
        assert (!kdTree.isEmpty());
        assert (kdTree.size() == 2);
        assert (kdTree.contains(new Point2D(0.2, 0.2)));
        assert (kdTree.contains(new Point2D(0.25, 0.4)));
        assert (!kdTree.contains(new Point2D(0.5, 0.5)));
        assert (!kdTree.contains(new Point2D(0.1, 0.1)));
        assert (!kdTree.contains(new Point2D(0.8, 0.4)));

        kdTree.insert(new Point2D(0.5, 0.5));
        assert (!kdTree.isEmpty());
        assert (kdTree.size() == 3);
        assert (kdTree.contains(new Point2D(0.2, 0.2)));
        assert (kdTree.contains(new Point2D(0.25, 0.4)));
        assert (kdTree.contains(new Point2D(0.5, 0.5)));
        assert (!kdTree.contains(new Point2D(0.1, 0.1)));
        assert (!kdTree.contains(new Point2D(0.8, 0.4)));

        kdTree.insert(new Point2D(0.1, 0.1));
        assert (!kdTree.isEmpty());
        assert (kdTree.size() == 4);
        assert (kdTree.contains(new Point2D(0.2, 0.2)));
        assert (kdTree.contains(new Point2D(0.25, 0.4)));
        assert (kdTree.contains(new Point2D(0.5, 0.5)));
        assert (kdTree.contains(new Point2D(0.1, 0.1)));
        assert (!kdTree.contains(new Point2D(0.8, 0.4)));


        // Test Draw Function
        kdTree.draw();

        // Test nearest neighbour function
        Point2D queryPoint, nearestpoint;
        queryPoint = new Point2D(0.05, 0.05);
        nearestpoint = kdTree.nearest(queryPoint);
        assert (nearestpoint.equals(new Point2D(0.1, 0.1)));
        queryPoint = new Point2D(0.9, 0.9);
        nearestpoint = kdTree.nearest(queryPoint);
        assert (nearestpoint.equals(new Point2D(0.5, 0.5)));
        StdOut.println("Nearest point from " + queryPoint + " is " + nearestpoint);
        queryPoint = new Point2D(0.3, 0.4);
        nearestpoint = kdTree.nearest(queryPoint);
        assert (nearestpoint.equals(new Point2D(0.25, 0.4)));
        StdOut.println("Nearest point from " + queryPoint + " is " + nearestpoint);

        // Test range search
        RectHV rangeRect;
        // Test rectangle 1
        rangeRect = new RectHV(0.0, 0.0, 0.15, 0.15);
        StdOut.println("Points that lie within the rectangle " + rangeRect);
        for (Point2D p : kdTree.range(rangeRect)) {
            StdOut.println("Point " + p);
        }
        // Test rectangle 2
        rangeRect = new RectHV(0.0, 0.0, 0.25, 0.30);
        StdOut.println("Points that lie within the rectangle " + rangeRect);
        for (Point2D p : kdTree.range(rangeRect)) {
            StdOut.println("Point " + p);
        }
        // Test rectangle 3
        rangeRect = new RectHV(0.0, 0.0, 0.3, 0.45);
        StdOut.println("Points that lie within the rectangle " + rangeRect);
        for (Point2D p : kdTree.range(rangeRect)) {
            StdOut.println("Point " + p);
        }


    }
}
