package ca.umanitoba.cs.oluwasef.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


/**
 * Represents a booking made by a Member for a  Resource within the library system.
 *A booking reserves a specific resource for a defined period of time
 * represented by a start and end timestamp.</p>
 **/
public class Booking {
    private final UUID id;
    private final Resource resource;
    private final Member member;
    private final Instant start;
    private final Instant end;

    // Constructor for booking class
    public Booking(UUID id, Resource resource, Member member, Instant start, Instant end) {
        this.id = Objects.requireNonNull(id);
        if (end.isBefore(start)) throw new IllegalArgumentException("end < start");
        this.resource = Objects.requireNonNull(resource);
        this.member = Objects.requireNonNull(member);
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
    }

    public UUID getId() { return id; }
    public Resource getResource() { return resource; }
    public Member getMember() { return member; }
    public Instant getStart() { return start; }
    public Instant getEnd() { return end; }

    /** True if this booking overlaps the other booking in time. */
    public boolean overlaps(Booking other) {
        Objects.requireNonNull(other);
        return !this.end.isBefore(other.start) && !other.end.isBefore(this.start);
    }
}
