package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.CellType;

import java.util.Objects;
/**
 * Represents a single cell (or tile) within a FloorMap.
 * Each  Cell describes a specific coordinate location on the library’s
 * floor map grid and has an associated CellType defining its purpose.
 */
public class Cell {
    private final int x; //horizontal coordinate
    private final int y; // vertical coordinate
    private final String label;
    private final CellType type;

    //constructor for cell with the given coordinates, label and type.
    public Cell(int x, int y, String label, CellType type) {
        this.x = x;
        this.y = y;
        this.label = Objects.requireNonNull(label);
        this.type = Objects.requireNonNull(type);
    }
    // return types
    public int getX() { return x; }
    public int getY() { return y; }
    public String getLabel() { return label; }
    public CellType getType() { return type; }
}
