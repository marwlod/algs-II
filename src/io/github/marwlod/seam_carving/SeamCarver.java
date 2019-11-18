package io.github.marwlod.seam_carving;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int[][] rgb;
    private Picture picture;
    private int picWidth;
    private int picHeight;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Picture cannot be null");
        this.picture = new Picture(picture);
        this.picWidth = this.picture.width();
        this.picHeight = this.picture.height();
        this.rgb = new int[picWidth][picHeight];
        for (int x = 0; x < picWidth; x++) {
            for (int y = 0; y < picHeight; y++) {
                rgb[x][y] = this.picture.getRGB(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picWidth;
    }

    // height of current picture
    public int height() {
        return picHeight;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > rgb.length - 1 || y < 0 || y > rgb[0].length - 1) throw new IllegalArgumentException("Points must be inside the picture");
        // all pixels at the perimeter has 1000 energy
        if (x == 0 || x == rgb.length - 1 || y == 0 || y == rgb[0].length - 1) return 1000;
        // all the others has energy calculated from neighboring pixels (right, left, up, down)
        int rgbLowerX = rgb[x-1][y];
        int rgbHigherX = rgb[x+1][y];
        int rgbLowerY = rgb[x][y-1];
        int rgbHigherY = rgb[x][y+1];
        int squaredGradX = getSquaredGradient(rgbLowerX, rgbHigherX);
        int squaredGradY = getSquaredGradient(rgbLowerY, rgbHigherY);
        return Math.sqrt(squaredGradX + squaredGradY);
    }

    // Rn(x,y)^2 + Gn(x,y)^2 + Bn(x,y)^2 where n = {x,y}, Rn(x,y) -> diff in red color between pixels
    // (x-1,y) and (x+1,y) for n = x or (x,y-1) and (x,y+1) for n = y
    private int getSquaredGradient(int firstRgb, int secondRgb) {
        int r = Math.abs(((firstRgb >> 16) & 0xFF) - ((secondRgb >> 16) & 0xFF));
        int g = Math.abs(((firstRgb >> 8) & 0xFF) - ((secondRgb >> 8) & 0xFF));
        int b = Math.abs((firstRgb & 0xFF) - (secondRgb & 0xFF));
        return r*r + g*g + b*b;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // could be improved to transpose only twice for many consecutive calls to this method
        transpose();
        int[] horizontalSeam = findVerticalSeam();
        transpose();
        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int width = rgb.length;
        int height = rgb[0].length;
        double[][] energies = new double[width][height];
        // calculate energies for all the vertices (pixels)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                energies[x][y] = energy(x, y);
            }
        }
        double[][] distTo = new double[width][height];
        // all vertices start out with "infinity" distance from first row, except the first row with dist 0
        for (int x = 0; x < width; x++) {
            for (int y = 1; y < height; y++) {
                distTo[x][y] = Integer.MAX_VALUE;
            }
        }
        int[][] pathTo = new int[width][height];
        for (int y = 0; y < height-1; y++) {
            for (int x = 0; x < width; x++) {
                double currMinEnergy = distTo[x][y] + energies[x][y];
                // relax three vertices below this vertex (if they exist at all) so they all have minimal possible energy
                if (x-1 >= 0 && distTo[x-1][y+1] > currMinEnergy) {
                    distTo[x-1][y+1] = currMinEnergy;
                    pathTo[x-1][y+1] = x;
                }
                if (distTo[x][y+1] > currMinEnergy) {
                    distTo[x][y+1] = currMinEnergy;
                    pathTo[x][y+1] = x;
                }
                if (x+1 < width && distTo[x+1][y+1] > currMinEnergy) {
                    distTo[x+1][y+1] = currMinEnergy;
                    pathTo[x+1][y+1] = x;
                }
            }
        }
        double minEnergy = Double.POSITIVE_INFINITY;
        int minEnergyV = Integer.MAX_VALUE;
        for (int x = 0; x < width; x++) {
            // find minimal total energy vertex at the bottom of the picture (last row)
            if (minEnergy > distTo[x][height-1]) {
                minEnergy = distTo[x][height-1];
                minEnergyV = x;
            }
        }
        // vertical seam defined as array of (x,y) where x = verticalSeam[i], y = i and i = (0, 1, ..., height-1)
        int[] verticalSeam = new int[height];
        verticalSeam[height-1] = minEnergyV;
        for (int i = height-2, y = height-1; i >= 0; i--, y--) {
            // go from vertex with minimal total energy at the bottom back to the top and construct the path
            verticalSeam[i] = pathTo[verticalSeam[i+1]][y];
        }
        return verticalSeam;
    }

    private void transpose() {
        int[][] transposed = new int[rgb[0].length][rgb.length];
        for (int x = 0; x < rgb.length; x++) {
            for (int y = 0; y < rgb[0].length; y++) {
                transposed[y][x] = rgb[x][y];
            }
        }
        this.rgb = transposed;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, picWidth, picHeight);
        Picture shorterPicture = new Picture(picWidth, picHeight-1);
        int[][] shorterRgb = new int[picWidth][picHeight-1];
        for (int x = 0; x < picWidth; x++) {
            for (int y = 0, oldY = 0; y < picHeight-1; y++, oldY++) {
                if (y == seam[x]) oldY++;
                shorterPicture.set(x, y, picture.get(x, oldY));
                shorterRgb[x][y] = rgb[x][oldY];
            }
        }
        picture = shorterPicture;
        rgb = shorterRgb;
        picHeight--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, picHeight, picWidth);
        Picture narrowerPicture = new Picture(picWidth-1, picHeight);
        int[][] narrowerRgb = new int[picWidth-1][picHeight];
        for (int y = 0; y < picHeight; y++) {
            for (int x = 0, oldX = 0; x < picWidth-1; x++, oldX++) {
                if (x == seam[y]) oldX++;
                narrowerPicture.set(x, y, picture.get(oldX, y));
                narrowerRgb[x][y] = rgb[oldX][y];
            }
        }
        picture = narrowerPicture;
        rgb = narrowerRgb;
        picWidth--;
    }

    private void validateSeam(int[] seam, int targetLength, int maxEntryValue) {
        if (seam == null || seam.length != targetLength) throw new IllegalArgumentException("Seam length is invalid");
        if (maxEntryValue < 2) throw new IllegalArgumentException("Cannot remove any more seams");
        for (int i = 0; i < seam.length; i++) {
            if ((i > 0 && Math.abs(seam[i] - seam[i-1]) > 1) || seam[i] < 0 || seam[i] > maxEntryValue-1)
                throw new IllegalArgumentException("Invalid seam content");
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver seamCarver = new SeamCarver(picture);
        System.out.println(seamCarver.energy(1, 2));
        System.out.println(seamCarver.energy(1, 1));
        System.out.println(seamCarver.energy(1, 3));

        int[] verticalSeam = seamCarver.findVerticalSeam();
        seamCarver.removeVerticalSeam(verticalSeam);

        int[] horizontalSeam = seamCarver.findHorizontalSeam();
        seamCarver.removeHorizontalSeam(horizontalSeam);
    }
}
