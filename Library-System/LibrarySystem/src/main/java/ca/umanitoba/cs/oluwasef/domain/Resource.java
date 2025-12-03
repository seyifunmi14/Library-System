package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.BookingConflictException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidResourceException;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a bookable resource within the library system, such as a study room,
 * computer station, or piece of specialized equipment.
 * <p>
 * This class is designed to be immutable, except for its internal {@code bookingList}
 * and {@code reviews} which are managed through dedicated methods. It utilizes a static
 * {@link ResourceBuilder} for safe instantiation.
 */
public final class Resource {

    /** A unique integer identifier for the resource. Must be greater than 0. */
    private final int resourceId;
    /** The human-readable name of the resource. Must not be null or empty. */
    private final String resourceName;
    /** The category or type of the resource (e.g., STUDY_ROOM, COMPUTER). */
    private final ResourceType kind;
    /** A detailed description of the resource. Must not be null or empty. */
    private final String description;
    /** The physical coordinate location of the resource within the library map. */
    private final Coordinate location;
    /**
     * A map storing current and future bookings. Keys are the unique Booking IDs.
     * This map is mutable but managed internally.
     */
    private final Map<Integer, Booking> bookingList;
    /** A list storing all reviews submitted for this resource. This list is mutable but managed internally. */
    private final List<Review> reviews;


    /**
     * A public static builder class used to enforce validation and facilitate the
     * clean creation of {@code Resource} objects.
     */
    public static final class ResourceBuilder {
        private int resourceId;
        private String resourceName;
        private ResourceType kind;
        private String desc;
        private Coordinate loc;

        /**
         * Sets the resource ID.
         * @param resourceId The unique ID of the resource.
         * @return The current builder instance.
         * @throws InvalidResourceException If the ID is less than or equal to 0.
         */
        public ResourceBuilder resourceId(int resourceId) {
            if(resourceId <= 0) {
                throw new InvalidResourceException();
            }
            this.resourceId = resourceId;
            return this;
        }

        /**
         * Sets the resource name and validates it (not null or blank).
         * @param resourceName The name of the resource.
         * @return The current builder instance.
         * @throws InvalidResourceException If the name is null or blank.
         */
        public ResourceBuilder resourceName(String resourceName) throws InvalidResourceException {
            Preconditions.checkNotNull(resourceName, "name cannot be null.");
            if (resourceName.isBlank()) {
                throw new InvalidResourceException();
            }
            this.resourceName = resourceName;
            return this;
        }

        /**
         * Sets the resource type.
         * @param kind The {@code ResourceType} of the resource.
         * @return The current builder instance.
         * @throws NullPointerException If the kind is null.
         */
        public ResourceBuilder kind(ResourceType kind) {
            Preconditions.checkNotNull(kind, "Resource type cannot be null.");
            this.kind = kind;
            return this;
        }

        /**
         * Sets the resource description and validates it (not null or blank).
         * @param desc The description of the resource.
         * @return The current builder instance.
         * @throws InvalidResourceException If the description is null or blank.
         */
        public ResourceBuilder description(String desc) throws InvalidResourceException {
            Preconditions.checkNotNull(desc, "description cannot be null.");
            if (desc.isBlank()) {
                throw new InvalidResourceException();
            }
            this.desc = desc;
            return this;
        }

        /**
         * Sets the physical location coordinates of the resource.
         * @param loc The {@code Coordinate} representing the location.
         * @return The current builder instance.
         * @throws NullPointerException If the location is null.
         */
        public ResourceBuilder location(Coordinate loc) {
            Preconditions.checkNotNull(loc, "location cannot be null.");
            this.loc = loc;
            return this;
        }

        /**
         * Constructs the final {@code Resource} object using the values provided to the builder.
         * The private constructor will perform a final validation check.
         * @return A newly constructed {@code Resource} object.
         */
        public Resource build() {
            return new Resource(resourceId, resourceName, kind, desc, loc);
        }
    }


    /**
     * Private constructor used exclusively by the {@link ResourceBuilder}.
     * Initializes the resource properties and internal collections.
     *
     * @param resourceId The ID of the resource.
     * @param resourceName The name of the resource.
     * @param kind The type of the resource.
     * @param description The resource description.
     * @param location The physical location coordinates.
     */
    private Resource(int  resourceId,
                     String resourceName,
                     ResourceType kind,
                     String description,
                     Coordinate location) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.kind = kind;
        this.description = description;
        this.location = location;
        this.bookingList = new LinkedHashMap<>();
        this.reviews = new ArrayList<>();
        // Post-condition check: ensure the new instance is valid
        checkResource();
    }

    /**
     * Checks all class invariants to ensure the Resource object is in a valid state.
     * This acts as the final validation step after construction and is called after
     * internal state changes (like adding a review).
     * @throws IllegalStateException If any class invariant is violated.
     */
    private void checkResource() {
        // Check core fields
        Preconditions.checkArgument(resourceId > 0, "Resource id must be greater than 0");
        Preconditions.checkNotNull(resourceName, "Resource name must not be null");
        Preconditions.checkArgument(!resourceName.isEmpty(), "name must not be empty");
        Preconditions.checkNotNull(kind, "Kind must not be null");
        Preconditions.checkNotNull(description, "Description must not be null");
        Preconditions.checkArgument(!description.isEmpty(), "description must not be empty");
        Preconditions.checkNotNull(location, "location must not be null");

        // Check collections and their contents
        Preconditions.checkNotNull(reviews, "reviews must not be null");
        Preconditions.checkNotNull(bookingList, "Booking List cannot be null");

        // Validate every item in the booking list
        for (Map.Entry<Integer, Booking> entry : bookingList.entrySet()) {
            Preconditions.checkNotNull(entry.getValue(), "Bookings in booking list cannot be null");
            Preconditions.checkNotNull(entry.getKey(), "Booking key can't be null");
            Preconditions.checkState(entry.getKey() > 0, "Booking key must be > 0");
            Preconditions.checkState(entry.getKey() == entry.getValue().bookingId(), "Booking key must match BookingID");
            Preconditions.checkState(entry.getValue().resourceId() == resourceId, "Booking Resource ID must match ResourceID");
        }
        // Validate every item in the reviews list
        for (Review r : reviews) {
            Preconditions.checkNotNull(r, "reviews list contains null review");
        }
    }

    // Accessors

    /**
     * Gets the coordinate location of the resource.
     * @return The {@code Coordinate} object representing the location.
     */
    public Coordinate getLocation() {
        return location;
    }

    /**
     * Gets the unique ID of the resource.
     * @return The resource ID.
     */
    public int getResourceId() { return resourceId; }

    /**
     * Gets the name of the resource.
     * @return The resource name.
     */
    public String getResourceName() { return resourceName; }

    /**
     * Gets the type/kind of the resource.
     * @return The {@code ResourceType}.
     */
    public ResourceType getKind() { return kind; }

    /**
     * Gets the description of the resource.
     * @return The description text.
     */
    public String getDescription() { return description; }

    /**
     * Gets the coordinate location of the resource (alias for {@link #getLocation()}).
     * @return The {@code Coordinate} object.
     */
    public Coordinate getCoordinate() { return location; }

    /**
     * Gets an unmodifiable view of the current booking list.
     * The map keys are the Booking IDs.
     * @return An unmodifiable Map of Booking IDs to Booking objects.
     */
    public Map <Integer, Booking> getBookingList() {
        return Collections.unmodifiableMap(this.bookingList);
    }

    /**
     * Gets an unmodifiable list of all reviews submitted for this resource.
     * @return An unmodifiable List of {@code Review} objects.
     */
    public List<Review> getReviews() { return Collections.unmodifiableList(reviews); }


    /**
     * Attempts to add a new booking to the resource's booking list.
     * The booking is only added if its time range does not conflict with any existing bookings.
     * @param booking The {@code Booking} object to be added.
     * @throws BookingConflictException If the given booking's time slot conflicts with an existing booking.
     */
    public void addBooking(Booking booking) throws BookingConflictException {
        for (Map.Entry<Integer, Booking> entry : bookingList.entrySet()) {
            // Overlap check: A conflicts B if A.start < B.end AND A.end > B.start
            boolean overlap = booking.start().isBefore(entry.getValue().end()) && booking.end().isAfter(entry.getValue().start());
            if (overlap) {
                throw new BookingConflictException();
            }
        }

        bookingList.put(booking.bookingId(), booking);
    }

    /**
     * Checks if the resource is available (not booked) during a specific time interval.
     * @param start The proposed start time.
     * @param end The proposed end time.
     * @return {@code true} if no bookings overlap with the interval; {@code false} otherwise.
     */
    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
        // Assume available until a conflict is found
        boolean available = true;
        for (Map.Entry<Integer, Booking> entry : bookingList.entrySet()) {
            // Overlap check
            boolean overlap = start.isBefore(entry.getValue().end()) && end.isAfter(entry.getValue().start());
            if (overlap) {
                available = false;
                break; // Exit immediately upon finding the first conflict
            }
        }
        return available;
    }
}