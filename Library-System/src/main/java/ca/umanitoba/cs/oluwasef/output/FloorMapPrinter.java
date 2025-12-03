package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.FloorMap;

/**
 * A floor map printer. Formats {@link FloorMap} information for display.
 */
public final class FloorMapPrinter {
    private final FloorMap map;
    private final String libraryName;

    /**
     * Constructs a FloorMapPrinter for the given floor map and library name.
     *
     * @param map the floor map to print
     * @param libraryName the name of the library associated with the map
     */
    public FloorMapPrinter(FloorMap map, String libraryName) {
        this.map = map;
        this.libraryName = libraryName;
    }

    /**
     * Prints this printer’s floor map to standard output.
     */
    public void print() {
        if (map == null) {
            System.out.println("No floor map available.");
            return;
        }

        System.out.println("═══════════════════════════════════════");
        System.out.println("          MAP of " + libraryName);
        System.out.println("═══════════════════════════════════════");

        char[][] grid = map.getGrid();
        for (char[] row : grid) {
            System.out.println(new String(row));
        }

        System.out.println("\n✨ A little legend:");
        System.out.println("   Follow 'E' to the 'D' and find wisdom among 'F'...");
        System.out.println("═══════════════════════════════════════");
    }
}