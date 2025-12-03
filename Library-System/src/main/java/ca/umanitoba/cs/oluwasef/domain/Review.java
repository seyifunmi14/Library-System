package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidReviewException;
import com.google.common.base.Preconditions;

/**
 * Immutable review for a {@link Media} (or possibly a {@link Resource}).
 *
 * @param memberId the ID of the member who created the review.
 * @param rating   the rating given, must be between 1 and 5 (inclusive).
 * @param text     the review text, must be non-null and non-empty.
 */
public record Review(int memberId, int rating, String text) {

    /**
     * Compact constructor – validates invariants.
     */
    public Review {
        Preconditions.checkArgument(memberId > 0, "Member Id must be greater than 0");
        Preconditions.checkNotNull(text, "text must not be null");
        Preconditions.checkArgument(!text.trim().isEmpty(), "text must be non-empty");
        Preconditions.checkArgument(rating >= 1 && rating <= 5,
                "rating must be between 1 and 5");
    }

    /**
     * Builder class for creating a validated {@link Review} object.
     * This is used to collect and validate review components before final construction.
     */
    public static class ReviewBuilder {

        private int memberId;
        private Integer rating;
        private String text;

        /**
         * Creates an empty ReviewBuilder instance.
         */
        public ReviewBuilder() { }

        /**
         * Sets the member ID for the review.
         *
         * @param id the member ID.
         * @return this builder instance.
         * @throws InvalidReviewException if the member ID is less than or equal to zero.
         */
        public ReviewBuilder memberId(int id) throws InvalidReviewException {
            if (id <= 0 ) {
                throw new InvalidReviewException();
            }
            this.memberId = id;
            return this;
        }

        /**
         * Sets the rating for the review.
         *
         * @param rating the rating value (must be between 1 and 5).
         * @return this builder instance.
         * @throws InvalidReviewException if the rating is not within the range [1, 5].
         */
        public ReviewBuilder rating(int rating) throws InvalidReviewException {
            if (!(rating >= 1 && rating <= 5)) {
                throw new InvalidReviewException();
            }
            this.rating = rating;
            return this;
        }

        /**
         * Sets the text content for the review.
         *
         * @param text the review text.
         * @return this builder instance.
         * @throws InvalidReviewException if the text is {@code null} or contains only whitespace.
         */
        public ReviewBuilder text(String text) throws InvalidReviewException {
            if (text == null || text.trim().isEmpty()) {
                throw new InvalidReviewException();
            }
            this.text = text;
            return this;
        }

        /**
         * Builds and returns an immutable {@link Review} object.
         *
         * @return a new Review instance with the accumulated and validated properties.
         * Note: The Review compact constructor will perform final invariant checks.
         */
        public Review build() {
            return new Review(memberId, rating, text);
        }
    }
}