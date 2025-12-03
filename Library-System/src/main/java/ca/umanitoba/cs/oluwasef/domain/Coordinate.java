package ca.umanitoba.cs.oluwasef.domain;

import com.google.common.base.Preconditions;

/**
 * Immutable coordinate in the floor map grid.
 *  @param row row cant change nor be < 1
 *  @param col   column cant change nor be < 1
 *  */
public record Coordinate(int row, int col) {
    public Coordinate{
        Preconditions.checkArgument(row >= 0, "Row must be greater than or equal to 0");
        Preconditions.checkArgument(col >= 0, "Column must be greater than or equal to 0");
    }
}