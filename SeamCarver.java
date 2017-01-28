
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.Arrays;

/*
 * Copyright (C) 2017 Michael <GrubenM@GMail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Michael <GrubenM@GMail.com>
 */
public class SeamCarver {
    Picture pic;
    double[][] energy;
    
    double[][] distTo;
    double distToSink;
    
    int[][] edgeTo;
    int edgeToSink;
    
    boolean transposed;
    
    /**
     * Create a seam carver object based on the given picture.
     * 
     * @param picture the given picture
     * @throws NullPointerException if the given picture is {@code null}.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) throw new java.lang.NullPointerException();
        
        // Defensively copy the given picture
        pic = new Picture(picture);
        transposed = false;
        
        // Set the dimensions of the distTo, edgeTo, and energy arrays
        distTo = new double[pic.height()][pic.width()];
        edgeTo = new int[pic.height()][pic.width()];
        energy = new double[pic.height()][pic.width()];
        
        // Pre-calculate the energy array
        for (int i = 0; i < pic.height(); i++) {
            for (int j = 0; j < pic.width(); j++) {
                energy[i][j] = calcEnergy(j, i);
            }
        }
    }
    
    /**
     * Current picture.
     * 
     * @return the current picture.
     */
    public Picture picture() {
        return pic;
    }
    
    /**
     * Width of current picture.
     * 
     * @return the width of the current picture.
     */
    public int width() {
        return pic.width();
    }
    
    /**
     * Height of current picture.
     * 
     * @return the height of the current picture.
     */
    public int height() {
        return pic.height();
    }
    
    /**
     * Energy of pixel at column x and row y.
     * 
     * Note that (0,0) is the pixel at the top-left corner of the image.
     * 
     * The dual-gradient energy function is used to compute the energy of a
     * pixel.
     * 
     * @param x
     * @param y
     * @return the energy of the pixel at column <em>x</em> and row <em>y</em>.
     * @throws IndexOutOfBoundsException if <em>x</em> is greater than
     *         or equal to the image width, if <em>y</em> is greater than or
     *         equal to the image height, or if <em>x</em> or <em>y</em> are
     *         negative.
     */
    public double energy(int x, int y) {        
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();
        
        return energy[y][x];
    }
        
    /**
     * Helper method to calculate the energy of pixel at column x and row y.
     * 
     * Note that (0,0) is the pixel at the top-left corner of the image.
     * 
     * The dual-gradient energy function is used to compute the energy of a
     * pixel.
     * 
     * @param x
     * @param y
     * @return the energy of the pixel at column <em>x</em> and row <em>y</em>.
     * @throws IndexOutOfBoundsException if <em>x</em> is greater than
     *         or equal to the image width, if <em>y</em> is greater than or
     *         equal to the image height, or if <em>x</em> or <em>y</em> are
     *         negative.
     */
    private double calcEnergy(int x, int y) {        
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();
                
        // Return 1000.0 for border pixels
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return (double) 1000;
        
        // Store pixel values in four [R,G,B] arrays.
        int[] up = getColorValues(pic.get(x, y - 1));
        int[] down = getColorValues(pic.get(x, y + 1));
        int[] left = getColorValues(pic.get(x - 1, y));
        int[] right = getColorValues(pic.get(x + 1, y));
        
        return Math.sqrt(gradient(up,down) + gradient(left,right));
    }
    
    /**
     * Returns a 3-integer array representing the [R,G,B] values of the given
     * Color.
     * 
     * @param c the given color
     * @return an integer array of [R,G,B]
     */
    private int[] getColorValues(Color c) {
        int[] a = new int[3];
        a[0] = c.getRed();
        a[1] = c.getGreen();
        a[2] = c.getBlue();
        return a;
    }
    
    /**
     * Returns the gradient computed from the two [R,G,B] arrays <em>a</em> and
     * <em>b</em>.
     * 
     * @param a
     * @param b
     * @return the gradient of <em>a</em> and <em>b</em>.
     */
    private double gradient(int[] a, int[] b) {
        return Math.pow(a[0] - b[0], 2) +
               Math.pow(a[1] - b[1], 2) +
               Math.pow(a[2] - b[2], 2);
    }
    
    /**
     * Sequence of indices for horizontal seam.
     * 
     * @return 
     */
    public int[] findHorizontalSeam() {
        return new int[0];
    }
    
    /**
     * Transposes the current picture
     */
    private void transpose() {
        transposed = !transposed;
    }
    
    /**
     * Sequence of indices for vertical seam.
     * 
     * @return 
     */
    public int[] findVerticalSeam() {
        
        // Reset our distTo and edgeTo values for a new search
        distToSink = Double.POSITIVE_INFINITY;
        edgeToSink = Integer.MAX_VALUE;
        for (double[] r: distTo) Arrays.fill(r, Double.POSITIVE_INFINITY);
        for (int[] r: edgeTo) Arrays.fill(r, Integer.MAX_VALUE);
        
        // Relax the entire top row
        Arrays.fill(distTo[0], (double) 1000);
        Arrays.fill(edgeTo[0], -1);
        
        // Visit all pixels from the top, diagonally to the right
        for (int top = width() - 1; top >= 0; top--) {
            for (int depth = 0;
                    depth + top < width() && depth < height();
                    depth++) {
                visit(depth, depth + top);
            }
        }
        // Visit all pixels from the left side, diagonally to the right
        for (int depth = 1; depth < height(); depth++) {
            for (int out = 0;
                    out < width() && depth + out < height();
                    out++) {
                visit(depth + out, out);
            }
        }
        
        // Add the path to the seam[]
        int[] seam = new int[height()];
        seam[height() - 1] = edgeToSink;
        
        for (int i = height() - 1; i > 0; i--) {
            seam[i - 1] = edgeTo[i][seam[i]];
        }
        
        return seam;
    }
    
    private void visit(int i, int j) {
        if (transposed) {
            // Only relax the sink
            if (j == width() - 1) {
                relax(i, j);
            }

            // Bottom edge; relax to the right and above
            else if (i == height() - 1) {
                relax(i, j, i, j + 1);
                relax(i, j, i - 1, j + 1);
            }

            // Top edge; relax to the right and below
            else if (i == 0) {
                relax(i, j, i, j + 1);
                relax(i, j, i + 1, j + 1);
            }

            // Middle pixel; relax right, below, and above
            else {
                relax(i, j, i - 1, j + 1);
                relax(i, j, i, j + 1);
                relax(i, j, i + 1, j + 1);
            }
        }
        
        else {
            // Only relax the sink
            if (i == height() - 1) {
                relax(i, j);
            }

            // Right edge; relax below and to the left
            else if (j == width() - 1) {
                relax(i, j, i + 1, j - 1);
                relax(i, j, i + 1, j);
            }

            // Left edge; relax below and to the right
            else if (j == 0) {
                relax(i, j, i + 1, j);
                relax(i, j, i + 1, j + 1);
            }

            // Middle pixel; relax left, below, and right
            else {
                relax(i, j, i + 1, j - 1);
                relax(i, j, i + 1, j);
                relax(i, j, i + 1, j + 1);
            }
        }
    }
    
    private void relax(int i, int j) {
        if (distToSink > distTo[i][j]) {
            distToSink = distTo[i][j];
            if (transposed) edgeToSink = i;
            else edgeToSink = j;
        }
    }
    
    private void relax(int i1, int j1, int i2, int j2) {
        if (distTo[i2][j2] > distTo[i1][j1] + energy[i2][j2]) {
            distTo[i2][j2] = distTo[i1][j1] + energy[i2][j2];
            if (transposed) edgeTo[i2][j2] = i1;
            else edgeTo[i2][j2] = j1;
        }
    }
    
    /**
     * Remove horizontal seam from current picture.
     * 
     * @param seam the given seam.
     * @throws NullPointerException if the given <em>seam</em> is {@code null}.
     * @throws IllegalArgumentException if the given <em>seam</em> does not
     *         match the picture width, if an index in the given <em>seam</em>
     *         is negative or is taller than the picture, or if two adjacent
     *         entries in the given <em>seam</em> differ by more than 1.
     */
    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too short");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != width())
            throw new java.lang.IllegalArgumentException("Invalid seam length");
        
        int yLast = seam[0];
        for (int y: seam) {
            if (y >= height() || y < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(y - yLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            yLast = y;
        }
        
        
    }
    
    /**
     * Remove vertical seam from current picture.
     * 
     * @param seam the given seam.
     * @throws NullPointerException if the given <em>seam</em> is {@code null}.
     * @throws IllegalArgumentException if the given <em>seam</em> does not
     *         match the picture height, if an index in the given <em>seam</em>
     *         is negative or is wider than the picture, or if two adjacent
     *         entries in the given <em>seam</em> differ by more than 1.
     */
    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too narrow");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != height())
            throw new java.lang.IllegalArgumentException("Invalid seam length");
        
        int xLast = seam[0];
        for (int x: seam) {
            if (x >= height() || x < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(x - xLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            xLast = x;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
