package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.domain.*;
import ca.umanitoba.cs.oluwasef.logic.MapManager;
import ca.umanitoba.cs.oluwasef.logic.MapManager.PathResult;
import ca.umanitoba.cs.oluwasef.output.LibraryPrinter;
import ca.umanitoba.cs.oluwasef.output.MediaPrinter;
import com.google.common.base.Preconditions;

import java.util.*;

/**
 * UI display for path-finding features.
 * Find a walking path to a media item in a library</li>
 * Find a walking path to a resource (study room, computer, etc.)
 * <p>All path computation is delegated to {@link MapManager};
 * this class only collects user choices and prints visual results.</p>
 */
public final class FindRouteDisplay {

    private final Scanner keyboard;
    private final MapManager mapManager;

    /**
     * Creates a new route-finding display bound to the active MapManager.
     *
     * @param keyboard the Scanner object used for reading user input. Must not be {@code null}.
     * @param mapManager the logic manager responsible for path computation. Must not be {@code null}.
     */
    public FindRouteDisplay(Scanner keyboard, MapManager mapManager) {
        this.keyboard = Preconditions.checkNotNull(keyboard);
        this.mapManager = Preconditions.checkNotNull(mapManager);
    }

    /**
     * Main loop for the “Find Route” screen.
     * Offers options to find a path to media, find a path to a resource, or exit.
     */
    public void run() {
        boolean keepGoing = true;

        while (keepGoing) {
            System.out.println();
            System.out.println("=== Find Route ===");
            System.out.println("1) Find path to a media");
            System.out.println("2) Find path to a resource");
            System.out.println("3) Back to previous menu");
            System.out.print("Choose an option: ");

            String choice = keyboard.nextLine().trim();

            switch (choice) {
                case "1" -> routeToMedia();
                case "2" -> routeToResource();
                case "3" -> keepGoing = false;
                default -> System.out.println("Unknown option: " + choice);
            }
        }
    }

    /**
     * Handles the interaction for finding a path to a specific media item.
     * Prompts the user for a library and media, then displays the resulting path.
     */
    private void routeToMedia() {
        LibrarySystem system = mapManager.getSystem();
        Library library = pickLibrary(system);
        if (library == null) {
            System.out.println("Cancelled: no library selected.");
            return;
        }

        Media media = pickMedia(library);
        if (media == null) {
            System.out.println("Cancelled: no media selected.");
            return;
        }

        // Delegates BFS/DFS and grid checking to MapManager.
        PathResult result = mapManager.findPathToMedia(library, media);
        if (result.getPath().isEmpty()) {
            System.out.println("No path could be found from the entrance to this media.");
            return;
        }

        printPathVisual(result, media.getCoordinate());
        printStepDirections(result.getPath());
        System.out.println();
        System.out.println("Route found to media: \"" + media.getTitle() + "\" at "
                + media.getCoordinate() + ".");
        System.out.println("Press Enter to return to the Find Route menu...");
        keyboard.nextLine();
    }


    /**
     * Handles the interaction for finding a path to a specific resource.
     * Prompts the user for a library and resource, then displays the resulting path.
     */
    private void routeToResource() {
        LibrarySystem system = mapManager.getSystem();
        Library library = pickLibrary(system);
        if (library == null) {
            System.out.println("Cancelled: no library selected.");
            return;
        }

        Resource resource = pickResource(library);
        if (resource == null) {
            System.out.println("Cancelled: no resource selected.");
            return;
        }

        PathResult result = mapManager.findPathToResource(library, resource);
        if (result.getPath().isEmpty()) {
            System.out.println("No path could be found from the entrance to this resource.");
            return;
        }

        printPathVisual(result, resource.getLocation());
        printStepDirections(result.getPath());
        System.out.println();
        System.out.println("Route found to resource: \""
                + resource.getResourceName() + "\" (" + resource.getKind() + ") at "
                + resource.getLocation() + ".");
        System.out.println("Press Enter to return to the Find Route menu...");
        keyboard.nextLine();
    }

    /**
     * Prompts the user to select a library from the system's list.
     *
     * @param system the LibrarySystem containing the available libraries.
     * @return the selected Library object, or {@code null} if the user cancels.
     */
    private Library pickLibrary(LibrarySystem system) {
        List<Library> list = new ArrayList<>(system.getLibraries().values());
        if (list.isEmpty()) {
            System.out.println("No libraries available.");
            return null;
        }

        System.out.println();
        System.out.println("--- Libraries ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.print((i + 1) + ") ");
            new LibraryPrinter(list.get(i)).print();
        }

        System.out.print("Choose a library (or 0 to cancel): ");
        int idx = readIntInRange(list.size());
        if (idx == 0) {
            return null;
        }
        return list.get(idx - 1);
    }

    /**
     * Prompts the user to select a media item from the library's list.
     *
     * @param library the library containing the media.
     * @return the selected Media object, or {@code null} if the user cancels.
     */
    private Media pickMedia(Library library) {
        List<Media> list = new ArrayList<>(library.getMedia());
        if (list.isEmpty()) {
            System.out.println("No media available in this library.");
            return null;
        }

        System.out.println();
        System.out.println("--- Media ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.print((i + 1) + ") ");
            new MediaPrinter(list.get(i)).print();
        }

        System.out.print("Choose media (or 0 to cancel): ");
        int idx = readIntInRange(list.size());
        return (idx == 0) ? null : list.get(idx - 1);
    }

    /**
     * Prompts the user to select a resource from the library's list, showing its type and coordinates.
     *
     * @param library the library containing the resources.
     * @return the selected Resource object, or {@code null} if the user cancels.
     */
    private Resource pickResource(Library library) {
        List<Resource> list = new ArrayList<>(library.getResources().values());
        if (list.isEmpty()) {
            System.out.println("No resources available in this library.");
            return null;
        }

        System.out.println();
        System.out.println("--- Resources ---");
        for (int i = 0; i < list.size(); i++) {
            Resource r = list.get(i);
            System.out.println((i + 1) + ") " + r.getResourceName()
                    + " (" + r.getKind() + ") at " + r.getLocation());
        }

        System.out.print("Choose resource (or 0 to cancel): ");
        int idx = readIntInRange(list.size());
        return (idx == 0) ? null : list.get(idx - 1);
    }

    /**
     * Prints a visual ASCII map of the library grid with the path overlaid.
     * The grid is cloned and marked with 'S' (start), 'X' (target), and '*' (path).
     *
     * @param result the PathResult containing the FloorMap and the path stack.
     * @param goal the coordinate of the target item (media or resource).
     */
    private void printPathVisual(PathResult result, Coordinate goal) {
        FloorMap map = result.getMap();
        char[][] original = map.getGrid();

        int height = original.length;
        int width = (height == 0) ? 0 : original[0].length;

        // Clone grid to draw on
        char[][] view = new char[height][width];
        for (int r = 0; r < height; r++) {
            System.arraycopy(original[r], 0, view[r], 0, width);
        }

        // Convert the stack to a list from start -> goal
        LinkedStack<Coordinate> stack = result.getPath();
        List<Coordinate> pathList = new ArrayList<>();
        // Note: The stack is popped here, then restored at the end of the method.
        while (!stack.isEmpty()) {
            pathList.add(0, stack.pop());
        }

        // Draw path
        if (!pathList.isEmpty()) {
            Coordinate start = pathList.get(0);

            for (Coordinate step : pathList) {
                int r = step.row();
                int c = step.col();

                if (step.equals(start)) {
                    view[r][c] = 'S';
                } else if (step.equals(goal)) {
                    view[r][c] = 'X';
                } else if (view[r][c] == ' ' || view[r][c] == '.') {
                    // Overwrite empty spaces or dots with the path marker
                    view[r][c] = '*';
                }
            }
        }

        System.out.println();
        System.out.println("Map legend: # = wall | S = entrance | X = target | * = path");
        System.out.println();

        for (int r = 0; r < height; r++) {
            System.out.println("  " + new String(view[r]));
        }

        // restore stack for next use
        for (Coordinate coord : pathList) {
            stack.push(coord);
        }
    }


    /**
     * Prints movement instructions (UP, DOWN, LEFT, RIGHT) based on coordinate differences
     * derived from the path stack. The stack is restored after printing.
     *
     * @param stack the LinkedStack of Coordinates representing the path from entrance to target.
     */
    private void printStepDirections(LinkedStack<Coordinate> stack) {
        List<Coordinate> list = new ArrayList<>();
        // Note: The stack is popped here, then restored at the end of the method.
        while (!stack.isEmpty()) {
            list.add(0, stack.pop());
        }

        if (list.size() < 2) {
            System.out.println("Path is trivial (already at the target).");
            return;
        }
        int steps = list.size() - 1;
        System.out.println();
        System.out.println("Step-by-step directions from entrance to target:");

        for (int i = 0; i < steps; i++) {
            Coordinate current = list.get(i);
            Coordinate next = list.get(i + 1);

            int dr = next.row() - current.row();
            int dc = next.col() - current.col();

            String dir;
            if (dr == -1 && dc == 0) {
                dir = "UP";
            } else if (dr == 1 && dc == 0) {
                dir = "DOWN";
            } else if (dr == 0 && dc == -1) {
                dir = "LEFT";
            } else if (dr == 0 && dc == 1) {
                dir = "RIGHT";
            } else {
                dir = "MOVE";
            }
            System.out.println("  " + (i + 1) + ") " + dir
                    + " to (" + next.row() + ", " + next.col() + ")");
        }

        // restore stack for next use
        for (Coordinate coord : list) {
            stack.push(coord);
        }
    }

    /**
     * Reads a line of input and attempts to parse it as an integer, ensuring it is within the range [0, max].
     * Repeats prompting until valid input is received.
     *
     * @param max the upper bound of the acceptable integer range (inclusive).
     * @return a valid integer between 0 and max.
     */
    private int readIntInRange(int max) {
        int value;
        do {
            String rawInput = keyboard.nextLine().trim();
            try {
                value = Integer.parseInt(rawInput);
                if (value < 0 || value > max) {
                    System.out.println("Enter a number between " + 0 + " and " + max + ".");
                    value = Integer.MIN_VALUE;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter digits only.");
                value = Integer.MIN_VALUE;
            }
        } while (value == Integer.MIN_VALUE);
        return value;
    }
}