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
    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
    public Picture picture()                          // current picture
    public     int width()                            // width of current picture
    public     int height()                           // height of current picture
    public  double energy(int x, int y)               // energy of pixel at column x and row y
    public   int[] findHorizontalSeam()               // sequence of indices for horizontal seam
    public   int[] findVerticalSeam()                 // sequence of indices for vertical seam
    public    void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
    public    void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
