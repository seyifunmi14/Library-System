package ca.umanitoba.cs.oluwasef.domain;

import java.util.List;
    /**
    * Adds a new Review to this object’s collection of reviews.
    *
    *  the review to add must not be null
    * throws NullPointerException if review is  null
    */
    public interface Reviewable {
        void addReview(Review r);
        List<Review> getReviews();
}
