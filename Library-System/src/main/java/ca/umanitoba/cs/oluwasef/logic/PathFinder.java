package ca.umanitoba.cs.oluwasef.logic;

import ca.umanitoba.cs.oluwasef.domain.Coordinate;
import ca.umanitoba.cs.oluwasef.domain.FloorMap;
import ca.umanitoba.cs.oluwasef.domain.LinkedStack;
import com.google.common.base.Preconditions;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Finds the shortest path on a FloorMap using BFS, moving in 4 directions
 * (up, down, left, right) and treating certain cells as walls.
 */
public final class PathFinder {

    private final FloorMap map;

    public PathFinder(FloorMap map) {
        this.map = Preconditions.checkNotNull(map, "map must not be null");
    }

    /**
     * Returns a stack of coordinates representing a path from start to goal.
     * - Bottom of the stack = start
     * - Top of the stack    = goal
     * If no path exists, the returned stack is empty.
     */
    public LinkedStack<Coordinate> findPath(Coordinate start, Coordinate goal) {
        Preconditions.checkNotNull(start, "start must not be null");
        Preconditions.checkNotNull(goal, "goal must not be null");

        char[][] grid = map.getGrid();
        int height = grid.length;
        if (height == 0) {
            return new LinkedStack<>();
        }
        int width = grid[0].length;

        // Bounds check for start & goal
        int sr = start.row();
        int sc = start.col();
        int gr = goal.row();
        int gc = goal.col();

        if (inBounds(sr, sc, height, width) || inBounds(gr, gc, height, width)) {
            // Out of map range → no path
            return new LinkedStack<>();
        }

        // sanity check: both start and goal must be walkable
        if (isWalkable(grid, sr, sc) || isWalkable(grid, gr, gc)) {
            return new LinkedStack<>();
        }

        boolean[][] visited = new boolean[height][width];
        Coordinate[][] parent = new Coordinate[height][width];  // used to reconstruct path

        Queue<Coordinate> queue = new ArrayDeque<>();

        visited[sr][sc] = true;
        parent[sr][sc] = null; // root of BFS
        queue.add(start);

        // 4-directional movement: up, down, left, right
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        boolean found = false;

        // Classic BFS
        while (!queue.isEmpty()) {
            Coordinate cur = queue.remove();
            int r = cur.row();
            int c = cur.col();

            // Goal found → stop BFS
            if (r == gr && c == gc) {
                found = true;
                break;
            }

            // Explore neighbors
            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k];
                int nc = c + dc[k];

                if (inBounds(nr, nc, height, width)) {
                    continue;
                }
                if (isWalkable(grid, nr, nc)) {   // blocked cell → skip
                    continue;
                }
                if (visited[nr][nc]) {
                    continue;
                }

                visited[nr][nc] = true;
                parent[nr][nc] = cur;
                queue.add(new Coordinate(nr, nc));
            }
        }

        LinkedStack<Coordinate> path = new LinkedStack<>();
        if (!found) {
            return path; // empty
        }

        // Reconstruct path from goal back to start using parent[][]
        Coordinate cur = goal;
        while (cur != null) {
            path.push(cur);
            int r = cur.row();
            int c = cur.col();
            cur = parent[r][c];
        }

        return path;
    }

    /**
     * A cell is walkable if it is NOT a wall / forbidden type.
     * Right now, only '#' is treated as a wall.
     * You can tweak this to also block staff-only areas, shelves, etc.
     */
    private boolean isWalkable(char[][] grid, int r, int c) {
        char cell = grid[r][c];
        return cell == '#' || cell == 'S' || cell == 'X';
    }

    /** Simple in-bounds helper. */
    private boolean inBounds(int r, int c, int height, int width) {
        return r < 0 || r >= height || c < 0 || c >= width;
    }
}