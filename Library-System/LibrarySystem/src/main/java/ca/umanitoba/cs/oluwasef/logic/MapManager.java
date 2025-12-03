package ca.umanitoba.cs.oluwasef.logic;

import ca.umanitoba.cs.oluwasef.domain.*;
import com.google.common.base.Preconditions;

/**
 * Logic layer for anything involving maps + paths.
 */
public final class MapManager {

    private final LibrarySystem system;

    /**
     * Initializes the map manager with the required system.
     *
     * @param system the library system context. Must not be {@code null}.
     */
    public MapManager(LibrarySystem system) {
        this.system = Preconditions.checkNotNull(system);
    }

    public LibrarySystem getSystem() {
        return system;
    }

    public static final class PathResult {
        private final FloorMap map;
        private final LinkedStack<Coordinate> path;

        /**
         * Creates a new path result.
         *
         * @param map  the map where the path was found. Must not be {@code null}.
         * @param path the stack of coordinates representing the path. Must not be {@code null}.
         */
        public PathResult(FloorMap map, LinkedStack<Coordinate> path) {
            this.map = Preconditions.checkNotNull(map);
            this.path = Preconditions.checkNotNull(path);
        }

        public FloorMap getMap() {
            return map;
        }

        public LinkedStack<Coordinate> getPath() {
            return path;
        }
    }

    /**
     * Calculates the path from the library entrance to a specific piece of Media.
     *
     * @param library the library entity containing the map. Must not be {@code null}.
     * @param media   the media item to find. Must not be {@code null}.
     * @return a PathResult containing the map and the path to the media.
     */
    public PathResult findPathToMedia(Library library, Media media) {
        Preconditions.checkNotNull(library, "library must not be null");
        Preconditions.checkNotNull(media, "media must not be null");

        FloorMap map = library.getMap();
        Coordinate start = map.getEntranceCoordinate();
        Coordinate goal = media.getCoordinate();

        PathFinder finder = new PathFinder(map);
        LinkedStack<Coordinate> path = finder.findPath(start, goal);

        return new PathResult(map, path);
    }

    /**
     * Calculates the path from the library entrance to a specific Resource.
     * @param library  the library entity containing the map. Must not be {@code null}.
     * @param resource the resource to find. Must not be {@code null}.
     * @return a PathResult containing the map and the path to the resource.
     */
    public PathResult findPathToResource(Library library, Resource resource) {
        Preconditions.checkNotNull(library, "library must not be null");
        Preconditions.checkNotNull(resource, "resource must not be null");

        FloorMap map = library.getMap();
        Coordinate start = map.getEntranceCoordinate();
        Coordinate goal = resource.getLocation();

        PathFinder finder = new PathFinder(map);
        LinkedStack<Coordinate> path = finder.findPath(start, goal);

        return new PathResult(map, path);
    }
}