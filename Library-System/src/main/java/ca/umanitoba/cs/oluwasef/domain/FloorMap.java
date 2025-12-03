package ca.umanitoba.cs.oluwasef.domain;

import com.google.common.base.Preconditions;

import java.util.Map;

/**
 * A library floor map. Floor maps show the physical layout of a {@link Library} branch.
 */
public final class FloorMap {
    private final int width;
    private final int height;
    private final char[][] grid;
    private final Map<Character, String> legend;


    /**
     * Constructs a floor map with the given dimensions and layout.
     *
     * @param width  the width of the floor map. Must be positive.
     * @param height the height of the floor map. Must be positive.
     * @param grid   the 2D character array representing the layout. Must not be {@code null}
     *               and must match the specified dimensions.
     * @param legend the mapping of characters to descriptions. Must not be {@code null}.
     * @throws IllegalArgumentException if width or height is not positive, or if grid dimensions don't match
     * @throws NullPointerException     if grid or legend is null
     */
    private FloorMap(int width, int height, char[][] grid, Map<Character, String> legend) {
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.legend = legend;
        checkFloorMap();
    }

    /**
     * Validates the floor map invariants.
     *
     * @throws IllegalStateException if any invariant is violated
     */
    private void checkFloorMap() {
        Preconditions.checkState(width > 0, "width must be positive");
        Preconditions.checkState(height > 0, "height must be positive");
        Preconditions.checkNotNull(grid, "grid must not be null");
        Preconditions.checkNotNull(legend, "legend must not be null");

        // Grid dimension and internal validity
        Preconditions.checkState(grid.length == height, "grid height must match specified height");
        for (int r = 0; r < grid.length; r++) {
            Preconditions.checkNotNull(grid[r], "grid row " + r + " must not be null");
            Preconditions.checkState(grid[r].length == width,
                    "grid row " + r + " width must match specified width");
        }

        // Legend validity: no null keys or values
        for (Map.Entry<Character, String> entry : legend.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "legend contains null key");
            Preconditions.checkNotNull(entry.getValue(), "legend contains null value for key " + entry.getKey());
            Preconditions.checkArgument(!entry.getValue().trim().isEmpty(),
                    "legend contains blank description for key " + entry.getKey());
        }
    }

    // Populate grid after building
    public void populateGrid() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (r == 0 || c == 0 || r == height - 1 || c == width - 1) {
                    grid[r][c] = '#';         // outer wall
                } else if (r == 1 && c == 1) {
                    grid[r][c] = 'E';         // entrance
                } else {
                    grid[r][c] = ' ';         // open area
                }
            }
        }
        checkFloorMap();
    }

    public char[][] getGrid() {
        return grid;
    }
    public Map<Character, String> getLegend() {
        return legend;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Coordinate getEntranceCoordinate() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == 'E') return new Coordinate(r, c);
            }
        }
        return null;
    }

    public static class FloorMapBuilder {
        private int width;
        private int height;
        private Map<Character, String> legend;

        public FloorMapBuilder width(int w) {
            Preconditions.checkArgument(w > 0, "width must be positive");
            this.width = w;
            return this;
        }

        public FloorMapBuilder height(int h) {
            Preconditions.checkArgument(h > 0, "height must be positive");
            this.height = h;
            return this;
        }

        public FloorMapBuilder legend(Map<Character, String> legend) {
            Preconditions.checkNotNull(legend);
            this.legend = legend;
            return this;
        }

        public FloorMap build() {
            // builder creates an *empty* grid, to be filled later
            char[][] blank = new char[height][width];
            return new FloorMap(width, height, blank, legend);
        }
    }
}