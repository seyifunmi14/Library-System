package ca.umanitoba.cs.oluwasef.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// Represents a written review made by a member for media or resource
public class Review {
    private final UUID id;
    private final Member author;
    private int rating;
    private String text;
    private final Instant createdAt;

    //Constructor for review class
    public Review(UUID id, Member author, int rating, String text, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.author = Objects.requireNonNull(author);
        this.rating = rating;
        this.text = Objects.requireNonNull(text);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    // return type
    public UUID getId() { return id; }
    public Member getAuthor() { return author; }
    public int getRating() { return rating; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }

    // Updates the rating of this review
    public void setRating(int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("rating must be between 1 and 5");
        this.rating = rating;
    }
    // Updates the text of this review
    public void setText(String text) {
        this.text = Objects.requireNonNull(text, "text must not be null");
        if (text.isBlank())
            throw new IllegalArgumentException("text must not be blank");
    }
}
