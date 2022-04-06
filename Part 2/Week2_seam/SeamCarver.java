/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;

    private boolean isTransposed;
    private int currWidth;
    private int currHeight;

    private static final double BORDER_PIXEL_ENERGY = 1000.0;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Picture input cannot be null");

        // Copy to private variable (assignment requirement)
        // The data type may not mutate the Picture argument to the constructor
        this.picture = new Picture(picture);

        isTransposed = false;
        currHeight = picture.height();
        currWidth = picture.width();
    }

    // current picture
    public Picture picture() {
        if (isTransposed) {
            transpose();
            return new Picture(picture);
        }
        else {
            return new Picture(picture);
        }
    }

    // width of original input picture
    public int width() {
        return (isTransposed) ?
               currHeight :
               currWidth;
    }

    // height of original input picture
    public int height() {
        return (isTransposed) ?
               currWidth :
               currHeight;
    }

    // energy of pixel at column hori and row vert
    // hori and vert is of the original input picture
    // hori is positive right, vert is positive down
    public double energy(int hori, int vert) {
        checkPixelBounds(hori, vert);
        if (isBorder(hori, vert)) {
            return BORDER_PIXEL_ENERGY;
        }

        int horiInput = hori;
        int vertInput = vert;
        if (isTransposed) {
            horiInput = vert;
            vertInput = hori;
        }

        int colorRight = picture.getRGB(horiInput + 1, vertInput);
        int colorLeft = picture.getRGB(horiInput - 1, vertInput);
        int rx = ((colorRight >> 16) & 0xFF) - ((colorLeft >> 16) & 0xFF);
        int gx = ((colorRight >> 8) & 0xFF) - ((colorLeft >> 8) & 0xFF);
        int bx = ((colorRight) & 0xFF) - ((colorLeft) & 0xFF);
        int delX = rx * rx + gx * gx + bx * bx;

        int colorTop = picture.getRGB(horiInput, vertInput + 1);
        int colorBtm = picture.getRGB(horiInput, vertInput - 1);
        int ry = ((colorTop >> 16) & 0xFF) - ((colorBtm >> 16) & 0xFF);
        int gy = ((colorTop >> 8) & 0xFF) - ((colorBtm >> 8) & 0xFF);
        int by = ((colorTop) & 0xFF) - ((colorBtm) & 0xFF);
        int delY = ry * ry + gy * gy + by * by;

        return Math.sqrt(delX + delY);
    }

    // energy of pixel at column hori and row vert
    // hori and vert is of the current picture
    private double energyPrivate(int hori, int vert) {
        return isTransposed ?
               energy(vert, hori) :
               energy(hori, vert);

    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (isTransposed) {
            return findVerticalSeamPrivate();
        }
        else {
            transpose();
            return findVerticalSeamPrivate();
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed) {
            transpose();
            return findVerticalSeamPrivate();
        }
        else {
            return findVerticalSeamPrivate();
        }
    }

    private int[] findVerticalSeamPrivate() {
        int height = currHeight;
        int width = currWidth;

        int[][] vertexTo = new int[height][width];
        double[][] distTo = new double[height][width];
        for (int vert = 0; vert < height; vert++) {
            for (int hori = 0; hori < width; hori++) {
                distTo[vert][hori] = vert == 0 ?
                                     0 :
                                     Double.POSITIVE_INFINITY;
            }
        }

        for (int vert = 0 /* vert refers to this.picture */; vert < (height - 1); vert++) {
            for (int hori = 0 /* hori refers to this.picture */; hori < (width - 1); hori++) {
                relaxVert(distTo, vertexTo, hori, vert);
            }
        }

        // Check bottom row for shortest distance
        int minHori = 0;
        double minDist = distTo[height - 1][0];
        for (int hori = 1; hori < width; hori++) {
            double dist = distTo[height - 1][hori];
            if (dist < minDist) {
                minHori = hori;
                minDist = dist;
            }
        }

        // Backtrack edgeTo to get seam
        int[] seam = new int[height];
        seam[height - 1] = minHori;
        for (int vert = height - 2; vert >= 0; vert--) {
            minHori = vertexTo[vert + 1][minHori];
            seam[vert] = minHori;
        }
        return seam;
    }

    // Computes the shortest distance and edge pointed to from given vertex (hori, vert)
    private void relaxVert(double[][] distTo, int[][] edgeTo, int hori, int vert) {
        int height = currHeight;
        int width = currWidth;

        // Relax bottom left
        if (hori > 0) {
            double dist = energyPrivate(hori - 1, vert + 1) + distTo[vert][hori];
            if (dist < distTo[vert + 1][hori - 1]) {
                distTo[vert + 1][hori - 1] = dist;
                edgeTo[vert + 1][hori - 1] = hori;
            }
        }
        // Relax below
        if (vert < height - 1) {
            double dist = energyPrivate(hori, vert + 1) + distTo[vert][hori];
            if (dist < distTo[vert + 1][hori]) {
                distTo[vert + 1][hori] = dist;
                edgeTo[vert + 1][hori] = hori;
            }
        }
        // Relax bottom right
        if (hori < width - 1) {
            double dist = energyPrivate(hori + 1, vert + 1) + distTo[vert][hori];
            if (dist < distTo[vert + 1][hori + 1]) {
                distTo[vert + 1][hori + 1] = dist;
                edgeTo[vert + 1][hori + 1] = hori;
            }
        }
    }

    // returns a Picture where each pixel is transposed
    // does not modify input Picture
    private void transpose() {
        // create new Picture object - don't modify source
        int width = currWidth;
        int height = currHeight;

        // We are transposing the picture
        Picture target = new Picture(height, width);

        // transpose EACH pixel around the vertical axis
        for (int hori = 0; hori < width; hori++) {
            for (int vert = 0; vert < height; vert++) {
                // get the pixel
                Color pixel = picture.get(hori, vert);
                // now set the pixel at the position flipped around vertically
                target.set(vert, hori, pixel);
            }
        }

        // Set private class variables
        isTransposed = !isTransposed;
        picture = target;

        int tempHeight = currHeight;
        currHeight = currWidth;
        currWidth = tempHeight;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, false);
        if (isTransposed) {
            removeVerticalSeamPrivate(seam);
        }
        else {
            transpose();
            removeVerticalSeamPrivate(seam);
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);
        if (isTransposed) {
            transpose();
            removeVerticalSeamPrivate(seam);
        }
        else {
            removeVerticalSeamPrivate(seam);
        }
    }

    private void validateSeam(int[] seam, boolean isVerticalSeam) {
        // Check to ensure that seam is
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        // Check minimum picture size
        if (isVerticalSeam && width() <= 1) {
            throw new IllegalArgumentException("Width is less than or equal to one");
        }
        if (!isVerticalSeam && height() <= 1) {
            throw new IllegalArgumentException("Height is less than or equal to one");
        }

        // Check seam length
        int expectedSeamLength = isVerticalSeam ? height() : width();
        if (expectedSeamLength != seam.length) {
            throw new IllegalArgumentException("Seam is not expected length");
        }

        int bounds = isVerticalSeam ? width() : height();
        for (int element : seam) {
            if (element < 0 || element >= bounds) {
                throw new IllegalArgumentException("element" + element + "is out of bounds");
            }
        }

        // Check to ensure that current element and previous element differs by at most one
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("Seam is not expected length");
            }
        }
    }

    private void removeVerticalSeamPrivate(int[] seam) {
        Picture resizedPicture = new Picture(currWidth - 1, currHeight);
        for (int vert = 0; vert < currHeight; vert++) {
            // Write to new picture pixels left of seam
            for (int hori = 0; hori < seam[vert]; hori++) {
                resizedPicture.set(hori, vert, picture.get(hori, vert));
            }
            // Write to new picture pixels right of seam
            for (int hori = seam[vert] + 1; hori < currWidth; hori++) {
                resizedPicture.set(hori - 1, vert, picture.get(hori, vert));
            }
        }
        // Replace current picture (before removal of seam) with new picture
        picture = resizedPicture;
        currWidth = currWidth - 1;
    }

    private int[] getNextVertSeam(int hori, int vert, int width) {
        if (vert == 1) {
            int[] retInt = new int[width];
            for (int i = 0; i < width; i++) {
                retInt[i] = i;
            }
            return retInt;
        }
        if (hori == 0) {
            return new int[] { 0, 1 };
        }
        if (hori == width - 1) {
            return new int[] { width - 2, width - 1 };
        }
        return new int[] { hori - 1, hori, hori + 1 };
    }

    // Private function to check pixel input bounds
    // Throw exception (assignment requirement) when out of bounds
    private void checkPixelBounds(int hori, int vert) {
        if (hori < 0 || hori > width() - 1 ||
                vert < 0 || vert > height() - 1) {
            throw new IllegalArgumentException("Width or Height Out of Bounds");
        }
    }

    // Private function to check if pixel input is along the border
    // hori and vert is of original input picture
    private boolean isBorder(int hori, int vert) {
        if (hori == 0 || hori == width() - 1 ||
                vert == 0 || vert == height() - 1) {
            return true;
        }
        return false;
    }

    private double computeVertSeamEnergy(int vertSeam[]) {
        double sumEnergy = 0.0;
        for (int vert = 0; vert < vertSeam.length; vert++) {
            sumEnergy += energy(vertSeam[vert], vert);
        }
        return sumEnergy;
    }

    private double computeHoriSeamEnergy(int horiSeam[]) {
        double sumEnergy = 0.0;
        for (int hori = 0; hori < horiSeam.length; hori++) {
            sumEnergy += energy(hori, horiSeam[hori]);
        }
        return sumEnergy;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        int[] seam;
        Picture picture;
        SeamCarver seamCarver;
        int[] vertSeamResult;
        int[] vertSeamResultExpected;
        double vertSeamEnergy;
        int[] horiSeamResult;
        int[] horiSeamResultExpected;
        double horiSeamEnergy;


        picture = new Picture("./seam/3x4.png");
        seamCarver = new SeamCarver(picture);
        StdOut.println("Energy(1,1) = " + seamCarver.energy(1, 1));
        assert (Math.abs(seamCarver.energy(1, 1) - Math.sqrt(52225)) < 1.0e-5);

        picture = new Picture("./seam/6x5.png");
        seamCarver = new SeamCarver(picture);
        vertSeamResult = seamCarver.findVerticalSeam();
        vertSeamEnergy = seamCarver.computeVertSeamEnergy(vertSeamResult);
        StdOut.println("Vert Result = " + Arrays.toString(vertSeamResult));
        StdOut.println("Seam Energy = " + vertSeamEnergy);
        assert (Math.abs(vertSeamEnergy - 2414.973495) < 1.0e-3);
        assert (vertSeamResult[0] == 3);
        assert (vertSeamResult[1] == 4);
        assert (vertSeamResult[2] == 3);
        assert (vertSeamResult[3] == 2);
        assert (vertSeamResult[4] == 1);
        horiSeamResult = seamCarver.findHorizontalSeam();
        horiSeamEnergy = seamCarver.computeHoriSeamEnergy(horiSeamResult);
        StdOut.println("Hori Result = " + Arrays.toString(horiSeamResult));
        StdOut.println("Seam Energy = " + horiSeamEnergy);
        assert (Math.abs(horiSeamEnergy - 2530.6819599) < 1.0e-3);
        assert (horiSeamResult[0] == 1);
        assert (horiSeamResult[1] == 2);
        assert (horiSeamResult[2] == 1);
        assert (horiSeamResult[3] == 2);
        assert (horiSeamResult[4] == 1);
        assert (horiSeamResult[5] == 0);
        seamCarver.removeHorizontalSeam(horiSeamResult);
        assert (seamCarver.width() == 6);
        assert (seamCarver.height() == 4);

        picture = new Picture("./seam/7x10.png");
        seamCarver = new SeamCarver(picture);
        vertSeamResult = seamCarver.findVerticalSeam();
        vertSeamEnergy = seamCarver.computeVertSeamEnergy(vertSeamResult);
        StdOut.println("Vert Result = " + Arrays.toString(vertSeamResult));
        StdOut.println("Seam Energy = " + vertSeamEnergy);
        vertSeamResultExpected = new int[] { 2, 3, 4, 3, 4, 3, 3, 2, 2, 1 };
        assert (Arrays.equals(vertSeamResult, vertSeamResultExpected));
        assert (Math.abs(vertSeamEnergy - 3443.197819) < 1.0e-3);
        horiSeamResult = seamCarver.findHorizontalSeam();
        horiSeamEnergy = seamCarver.computeHoriSeamEnergy(horiSeamResult);
        StdOut.println("Hori Result = " + Arrays.toString(horiSeamResult));
        StdOut.println("Seam Energy = " + horiSeamEnergy);
        horiSeamResultExpected = new int[] { 6, 7, 7, 7, 8, 8, 7 };
        assert (Arrays.equals(horiSeamResult, horiSeamResultExpected));
        assert (Math.abs(horiSeamEnergy - 2898.313922) < 1.0e-3);
        seamCarver.removeVerticalSeam(seamCarver.findVerticalSeam());
        assert (seamCarver.width() == 6);
        assert (seamCarver.height() == 10);

        picture = new Picture("./seam/8x1.png");
        seamCarver = new SeamCarver(picture);
        horiSeamResult = seamCarver.findHorizontalSeam();
        horiSeamEnergy = seamCarver.computeHoriSeamEnergy(horiSeamResult);
        horiSeamResultExpected = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        assert (Arrays.equals(horiSeamResult, horiSeamResultExpected));
        assert (Math.abs(horiSeamEnergy - 8000.0) < 1.0e-3);

        picture = new Picture("./seam/1x8.png");
        seamCarver = new SeamCarver(picture);
        seam = new int[] { 0 };
        seamCarver.removeHorizontalSeam(seam);

    }
}
