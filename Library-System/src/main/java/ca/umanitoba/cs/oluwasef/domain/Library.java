package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import com.google.common.base.Preconditions;

import java.util.*;

/**
 * A library branch. Libraries contain {@link Media}, {@link Resource} items, and a {@link FloorMap}.
 */
public final class Library {

    private final UUID id;
    private final String name;
    private final String address;
    private final FloorMap map;
    private final Map<String, Media> media = new HashMap<>();
    private final Map<Integer, Resource> resources = new HashMap<>();

    /**
     * Constructs a library with the given properties.
     *
     * @param id the unique identifier for the library. Must not be {@code null}.
     * @param name the name of the library. Must not be {@code null} or empty.
     * @param address the address of the library. Must not be {@code null} or empty.
     * @param map the floor map of the library. Must not be {@code null}.
     * @throws IllegalArgumentException if name or address is empty
     * @throws NullPointerException if any parameter is null
     */
    private Library(UUID id, String name, String address, FloorMap map) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.map = map;

        Preconditions.checkArgument(!name.isEmpty(), "name must be non-empty");
        Preconditions.checkArgument(!address.isEmpty(), "address must be non-empty");
        Preconditions.checkNotNull(map, "map must not be null");
        checkLibrary();
    }

    /**
     * Validates the library invariants.
     *
     * @throws IllegalStateException if any invariant is violated
     */
    private void checkLibrary() {
        Preconditions.checkNotNull(id, "id must not be null");
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkArgument(!name.isEmpty(), "name must be non-empty");
        Preconditions.checkNotNull(address, "address must not be null");
        Preconditions.checkArgument(!address.isEmpty(), "address must be non-empty");
        Preconditions.checkNotNull(map, "map must not be null");
        Preconditions.checkNotNull(media, "media must not be null");
        Preconditions.checkNotNull(resources, "resources must not be null");

        for (Map.Entry<String, Media> entry : media.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Null key in media map");
            Preconditions.checkNotNull(entry.getValue(), "Null Media in media map");
        }
        for (Map.Entry<Integer, Resource> entry : resources.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Null key in resources map");
            Preconditions.checkNotNull(entry.getValue(), "Null Resource in resources map");
        }
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public FloorMap getMap() {return map;}

    public Collection<Media> getMedia() {return Collections.unmodifiableMap(media).values();}
    public Map<Integer, Resource> getResources() {return Collections.unmodifiableMap(resources);}

    /**
     * Adds a media item to this library.
     *
     * @param m the media item to add. Must not be {@code null} and must have a non-null ID.
     * @throws IllegalArgumentException if media ID is null
     * @throws NullPointerException if media is null
     * @post media is present in the library
     */
    public void addMedia(Media m) {
        Preconditions.checkNotNull(m, "media must not be null");
        Preconditions.checkNotNull(m.getId(), "media ID must not be null");

        media.put(m.getId().toString(), m);
        checkLibrary();

        //Post-condition check
        Preconditions.checkState(media.containsKey(m.getId().toString()), "media must be present after addition");
    }

    /**
     * Adds a resource to this library.
     *
     * @param r the resource to add. Must not be {@code null} and must have a non-null ID.
     * @throws IllegalArgumentException if resource ID is null
     * @throws NullPointerException if resource is null
     * @post resource is present in the library
     */
    public void addResource(Resource r) {
        Preconditions.checkNotNull(r, "resource must not be null");
        Preconditions.checkArgument(r.getResourceId() > 0, "resource ID must be greater than 0");

        resources.put(r.getResourceId(), r);
        checkLibrary();

        // Post-condition check
        Preconditions.checkState(resources.containsKey(r.getResourceId()),
                "resource must be present after addition");
    }

    public Media requireMedia(String mediaId) throws EntityNotFoundException{
        Media m = media.get(mediaId); // media is your Map<String, Media>
        if (m == null) {
            throw new EntityNotFoundException();
        }
        return m;
    }

    public Resource requireResource(int resourceId) throws EntityNotFoundException {
        Resource r = resources.get(resourceId);
        if (r == null) {
            throw new EntityNotFoundException();
        }
        return r;
    }
    /**
     * Builder for constructing Library objects cleanly.
     */
    public static final class LibraryBuilder {

        private UUID id;
        private String name;
        private String address;
        private FloorMap map;

        public LibraryBuilder() {}

        public LibraryBuilder id(UUID id) {
            Preconditions.checkNotNull(id, "Library ID cannot be null");
            this.id = id;
            return this;
        }

        public LibraryBuilder name(String name) {
            Preconditions.checkNotNull(name, "Library name cannot be null");
            Preconditions.checkArgument(!name.isBlank(), "Library name cannot be blank");
            this.name = name;
            return this;
        }

        public LibraryBuilder address(String address) {
            Preconditions.checkNotNull(address, "Library address cannot be null");
            Preconditions.checkArgument(!address.isBlank(), "Library address cannot be blank");
            this.address = address;
            return this;
        }

        public LibraryBuilder map(FloorMap map) {
            Preconditions.checkNotNull(map, "Library map cannot be null");
            this.map = map;
            return this;
        }
        public Library build() {
            return new Library(id, name, address, map);
        }
    }

}