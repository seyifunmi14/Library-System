package ca.umanitoba.cs.oluwasef.domain;

import java.util.*;

public abstract class Resource implements Reviewable {
    public final UUID id;
    public String name;
    public String description;
    public final List<Review> reviews = new ArrayList<>();

    public Resource(UUID id, String name, String description) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setDescription(String description) { this.description = Objects.requireNonNull(description); }

    public void addReview(Review r) { reviews.add(Objects.requireNonNull(r)); }
    public List<Review> getReviews() { return Collections.unmodifiableList(reviews); }
}
