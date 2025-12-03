package ca.umanitoba.cs.oluwasef.domain;

import java.util.*;

// Represents the floor layout of the library.
public class FloorMap {
    private final int width;//horizontal cells
    private final int height; //vertical cells
    private final Cell[] cells; // array storing all cells
    private final Map<CellType, String> legend = new EnumMap<>(CellType.class);

    //Constructor of a new FloorMap with the given dimension
    public FloorMap(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("invalid size");
        this.width = width;
        this.height = height;
        this.cells = new Cell[width * height];
        // initialize every cell as empty
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[index(x, y)] = new Cell(x, y, "", CellType.EMPTY);
            }
        }
    }

    //Computes the index in the 1D array for the given (x, y) coordinate
    private int index(int x, int y) {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<CellType, String> getLegend() {
        return legend;
    }

    public Cell cellAt(int x, int y) {
        validate(x, y);
        return cells[index(x, y)];
    }
    //Validates that the provided coordinates are within map bounds.
    private void validate(int x, int y) {
        if (x < 0 || x >= width)
            throw new IndexOutOfBoundsException("x out of bounds: " + x);
        if (y < 0 || y >= height)
            throw new IndexOutOfBoundsException("y out of bounds: " + y);
    }
}    
    