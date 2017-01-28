
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
    int[][] energy;
    
    int[][] distTo;
    int distToSink;
    
    int[][] edgeTo;
    int edgeToSink;
    
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
        
        
        // Set the dimensions of the distTo and edgeTo arrays
        distTo = new int[pic.height()][pic.width()];
        edgeTo = new int[pic.height()][pic.width()];
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
        if (x >= pic.width() || y >= pic.height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();
        
        // Return 1000.0 for border pixels
        if (x == 0 || y == 0 || x == pic.width() - 1 || y == pic.height() - 1)
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
        
    }
    
    /**
     * Sequence of indices for vertical seam.
     * 
     * @return 
     */
    public int[] findVerticalSeam() {
        
        // Reset our distTo and edgeTo values for a new search
        distToSink = Integer.MAX_VALUE;
        edgeToSink = Integer.MAX_VALUE;
        for (int[] r: distTo) Arrays.fill(r, Integer.MAX_VALUE);
        for (int[] r: edgeTo) Arrays.fill(r, Integer.MAX_VALUE);
        
        
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
        if (pic.height() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too short");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != pic.width())
            throw new java.lang.IllegalArgumentException("Invalid seam length");
        
        int yLast = seam[0];
        for (int y: seam) {
            if (y >= pic.height() || y < 0)
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
        if (pic.width() <= 1)
            throw new java.lang.IllegalArgumentException("Picture too narrow");
        if (seam == null) throw new java.lang.NullPointerException();
        if (seam.length != pic.height())
            throw new java.lang.IllegalArgumentException("Invalid seam length");
        
        int xLast = seam[0];
        for (int x: seam) {
            if (x >= pic.height() || x < 0)
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
