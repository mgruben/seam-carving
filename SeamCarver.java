
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
    private Picture pic;
    private Color[][] color;
    private double[][] energy;
    
    private double[][] distTo;
    private double distToSink;
    
    private int[][] edgeTo;
    private int edgeToSink;
    
    private int w;
    private int h;
    
    private boolean transposed;
    
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
        
        // Initialize the dimensions of the picture
        w = pic.width();
        h = pic.height();
        
        // Store the picture's color information 
        color = new Color[h][w];
        
        // Set the dimensions of the distTo, edgeTo, and energy arrays
        energy = new double[h][w];
        
        // Store color information
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                color[i][j] = pic.get(j, i);
            }
        }
        
        // Pre-calculate the energy array
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
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
        // If pic is unchanged, return a defensive copy of pic
        if (height() == pic.height() && width() == pic.width())
            return new Picture(pic);
        
        // Otherwise, create a new pic with the updated color information,
        // and return a defensive copy of that pic
        else {
            pic = new Picture(width(), height());
            for (int i = 0; i < height(); i++) {
                for (int j = 0; j < width(); j++) {
                    pic.set(j, i, color[i][j]);
                }
            }
            return new Picture(pic);
        }
    }
    
    /**
     * Width of current picture.
     * 
     * @return the width of the current picture.
     */
    public int width() {
        return w;
    }
    
    /**
     * Height of current picture.
     * 
     * @return the height of the current picture.
     */
    public int height() {
        return h;
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
        int[] up = getColorValues(color[y - 1][x]);
        int[] down = getColorValues(color[y + 1][x]);
        int[] left = getColorValues(color[y][x - 1]);
        int[] right = getColorValues(color[y][x + 1]);
        
        return Math.sqrt(gradient(up, down) + gradient(left, right));
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
     * @return the sequence of indices for the horizontal seam.
     */
    public int[] findHorizontalSeam() {
        transposed = true;

        // Reset our distTo and edgeTo values for a new search
        distToSink = Double.POSITIVE_INFINITY;
        edgeToSink = Integer.MAX_VALUE;
        distTo = new double[h][w];
        edgeTo = new int[h][w];
        for (double[] r: distTo) Arrays.fill(r, Double.POSITIVE_INFINITY);
        for (int[] r: edgeTo) Arrays.fill(r, Integer.MAX_VALUE);
        
        // Relax the entire left column
        for (int i = 0; i < height(); i++) {
            distTo[i][0] = (double) 1000;
            edgeTo[i][0] = -1;
        }
        
        // Visit all pixels from the left side, diagonally to the right
        for (int depth = height() - 1; depth > 0; depth--) {
            for (int out = 0;
                    out < width() && depth + out < height();
                    out++) {
                visit(depth + out, out);
            }
        }
        
        // Visit all pixels from the top, diagonally to the right
        for (int top = 0; top < width(); top++) {
            for (int depth = 0;
                    depth + top < width() && depth < height();
                    depth++) {
                visit(depth, depth + top);
            }
        }
        
        // Add the path to the seam[]
        int[] seam = new int[width()];
        seam[width() - 1] = edgeToSink;
        
        for (int j = width() - 1; j > 0; j--) {
            seam[j - 1] = edgeTo[seam[j]][j];
        }
        
        return seam;

    }
    
    /**
     * Sequence of indices for vertical seam.
     * 
     * @return the sequence of indices for the vertical seam.
     */
    public int[] findVerticalSeam() {
        transposed = false;
        
        // Reset our distTo and edgeTo values for a new search
        distToSink = Double.POSITIVE_INFINITY;
        edgeToSink = Integer.MAX_VALUE;
        distTo = new double[h][w];
        edgeTo = new int[h][w];
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
    
    /**
     * Given an index, relax the vertices adjacent to that index.
     * 
     * @param i the up-and-down index
     * @param j the left-and-right index
     */
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
    
    /**
     * Given an index, relax the sink vertex from that index.
     * 
     * This method should only be called on the "last" vertices in the image.
     * 
     * @param i the up-and-down index
     * @param j the left-and-right index
     */
    private void relax(int i, int j) {
        if (validIndex(i, j)) {
            if (distToSink > distTo[i][j]) {
                distToSink = distTo[i][j];
                if (transposed) edgeToSink = i;
                else edgeToSink = j;
            }
        }
    }
    
    /**
     * Given index 1 and index 2, relaxes index 2 from index 1.
     * 
     * This method should not be called on the "last" vertices in the image.
     * 
     * @param i1 the up-and-down index of index 1
     * @param j1 the left-and-right index of index 1
     * @param i2 the up-and-down index of index 2
     * @param j2 the left-and-right index of index 1
     */
    private void relax(int i1, int j1, int i2, int j2) {
        if (validIndex(i1, j1) && validIndex(i2, j2)) {
            if (distTo[i2][j2] > distTo[i1][j1] + energy[i2][j2]) {
                distTo[i2][j2] = distTo[i1][j1] + energy[i2][j2];
                if (transposed) edgeTo[i2][j2] = i1;
                else edgeTo[i2][j2] = j1;
            }
        }
    }
    
    /**
     * Is the given index valid?
     * @param i the up-and-down index
     * @param j the left-and-right index
     * @return {@code true} if the current picture contains the index,
     *         {@code false} otherwise.
     */
    private boolean validIndex(int i, int j) {
        return (i >= 0 && i < height() && j >= 0 && j < width());
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
        
        Color[][] newColor = new Color[height() - 1][width()];
        double[][] newEnergy = new double[height() - 1][width()];
        
        for (int j = 0; j < width(); j++) {
            int s = seam[j];
            for (int i = 0; i < s; i++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }
            
            for (int i = s + 1; i < height(); i++) {
                newColor[i - 1][j] = color[i][j];
                newEnergy[i - 1][j] = energy[i][j];
            }
        }
        
        color = newColor;
        energy = newEnergy;
        h--;
        
        // Recalculate the energy along the seam
        for (int j = 0; j < width(); j++) {
            int s = seam[j];
            // Top edge removed
            if (s == 0) {
                energy[s][j] = calcEnergy(j, s);
            }
            
            // Bottom edge removed
            else if (s == height()) {
                energy[s - 1][j] = calcEnergy(j, s - 1);
            }
            
            // Middle pixel removed
            else {
                energy[s][j] = calcEnergy(j, s);
                energy[s - 1][j] = calcEnergy(j, s - 1);
            }
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
            if (x >= width() || x < 0)
                throw new java.lang.IllegalArgumentException("Index out of bounds");
            if (Math.abs(x - xLast) > 1)
                throw new java.lang.IllegalArgumentException("Index not adjacent");
            xLast = x;
        }
                
        Color[][] newColor = new Color[height()][width() - 1];
        double[][] newEnergy = new double[height()][width() - 1];
        
        for (int i = 0; i < height(); i++) {
            int s = seam[i];
            
            for (int j = 0; j < s; j++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }
            
            for (int j = s + 1; j < width(); j++) {
                newColor[i][j - 1] = color[i][j];
                newEnergy[i][j - 1] = energy[i][j];
            }
        }
        
        color = newColor;
        energy = newEnergy;
        w--;
        
        // Recalculate the energy along the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];
            
            // Left edge removed
            if (s == 0) {
                energy[i][s] = calcEnergy(s, i);
            }
            
            // Right edge removed
            else if (s == width()) {
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }
            
            // Middle pixel removed
            else {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }
        }        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
