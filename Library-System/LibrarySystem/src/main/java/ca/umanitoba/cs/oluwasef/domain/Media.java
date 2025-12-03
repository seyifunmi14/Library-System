package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidMediaException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidReviewException;
import com.google.common.base.Preconditions;

import java.util.*;

/**
 * A piece of media. Media items have physical {@link Copy} copies and can receive {@link Review} feedback.
 */
public class Media {

    private final UUID id;
    private final String title;
    private final String creator;
    private final MediaType kind;
    private final MediaCategory category;
    private final Coordinate location;

    /**
     * The number of copies to auto-create when a new media item is built.
     */
    private static final int AVAILABLE_COPIES = 50;

    /**
     * Map of physical copies of this media. Key = copy barcode, Value = Copy object.
     */
    private final Map<String, Copy> mediaCopies;

    /**
     * Queue of members waiting to borrow this media item.
     */
    private final Deque<Member> waitlist = new ArrayDeque<>();

    /**
     * List of {@link Review}s submitted for this media item.
     */
    private final List<Review> reviews = new ArrayList<>();

    /**
     * Constructs a media item with the given properties.
     *
     * @param id the unique ID of the media item.
     * @param title the title of the media item.
     * @param creator the creator (author/director/artist) of the media item.
     * @param kind the type of media (e.g., BOOK, DVD).
     * @param category the category of the media (e.g., FICTION, BIOGRAPHY).
     * @param location the physical coordinate of the media item in the library map.
     */
    private Media(UUID id,
                  String title,
                  String creator,
                  MediaType kind,
                  MediaCategory category,
                  Coordinate location) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.kind = kind;
        this.category = category;
        this.location = location;
        this.mediaCopies = new LinkedHashMap<>();
        checkMedia();
    }


    /**
     * Builder class for safely constructing and validating new {@link Media} objects.
     */
    public static final class MediaBuilder {

        private UUID id;
        private String title;
        private String creator;
        private MediaType kind;
        private MediaCategory category;
        private Coordinate location;

        /**
         * Creates an empty MediaBuilder instance.
         */
        public MediaBuilder() { }

        /**
         * Sets the unique ID for the media.
         *
         * @param id the UUID. Must not be {@code null}.
         * @return this builder instance.
         */
        public MediaBuilder id(UUID id) {
            Preconditions.checkNotNull(id, "id cannot be null.");
            this.id = id;
            return this;
        }

        /**
         * Sets the title of the media item.
         *
         * @param title the title string. Must be non-null and non-blank.
         * @return this builder instance.
         * @throws InvalidMediaException if the title is blank.
         */
        public MediaBuilder title(String title) throws InvalidMediaException {
            Preconditions.checkNotNull(title, "title cannot be null.");
            if (title.trim().isEmpty()) {
                throw new InvalidMediaException();
            }
            this.title = title.trim();
            return this;
        }

        /**
         * Sets the creator (author/director/artist) of the media item.
         *
         * @param creator the creator string. Must be non-null and non-blank.
         * @return this builder instance.
         * @throws InvalidMediaException if the creator is blank.
         */
        public MediaBuilder creator(String creator) throws InvalidMediaException {
            Preconditions.checkNotNull(creator, "creator cannot be null.");
            if (creator.trim().isEmpty()) {
                throw new InvalidMediaException();
            }
            this.creator = creator.trim();
            return this;
        }

        /**
         * Sets the type of media.
         *
         * @param kind the {@link MediaType} enum value. Must not be {@code null}.
         * @return this builder instance.
         */
        public MediaBuilder kind(MediaType kind) {
            Preconditions.checkNotNull(kind, "kind (media type) cannot be null.");
            this.kind = kind;
            return this;
        }

        /**
         * Sets the category of the media.
         *
         * @param category the {@link MediaCategory} enum value. Must not be {@code null}.
         * @return this builder instance.
         */
        public MediaBuilder category(MediaCategory category) {
            Preconditions.checkNotNull(category, "category cannot be null.");
            this.category = category;
            return this;
        }

        /**
         * Sets the physical location coordinate of the media item.
         *
         * @param location the {@link Coordinate} object. Must not be {@code null}.
         * @return this builder instance.
         */
        public MediaBuilder location(Coordinate location) {
            Preconditions.checkNotNull(location, "location cannot be null.");
            this.location = location;
            return this;
        }

        /**
         * Builds a new Media object and automatically attaches a default number
         * of available copies with generated barcodes.
         *
         * @return a new fully constructed {@link Media} object.
         */
        public Media build() {
            UUID mediaId;
            mediaId = Objects.requireNonNullElseGet(this.id, UUID::randomUUID);


            Coordinate loc;
            loc = Objects.requireNonNullElseGet(this.location, () -> new Coordinate(1, 0));

            Media m = new Media(mediaId, title, creator, kind, category, loc);
            for (int i = 1; i <= AVAILABLE_COPIES; i++) {
                String barcode = mediaId + "-C" + i;
                m.addCopy(new Copy(barcode));
            }
            return m;
        }
    }
    /**
     * Class invariants for Media.
     * Ensures all internal state (ID, title, location, and collection contents) is valid.
     */
    private void checkMedia() {
        Preconditions.checkNotNull(id, "id must not be null");
        Preconditions.checkNotNull(title, "title must not be null");
        Preconditions.checkArgument(!title.isEmpty(), "title must be non-empty");
        Preconditions.checkNotNull(creator, "creator must not be null");
        Preconditions.checkArgument(!creator.isEmpty(), "creator must be non-empty");
        Preconditions.checkNotNull(kind, "kind must not be null");
        Preconditions.checkNotNull(category, "category must not be null");
        Preconditions.checkNotNull(location, "location must not be null");
        Preconditions.checkNotNull(waitlist, "waitlist must not be null");
        Preconditions.checkNotNull(reviews, "reviews must not be null");
        Preconditions.checkNotNull(mediaCopies, "mediaCopies must not be null");

        // Copies
        for (Map.Entry<String, Copy> entry : mediaCopies.entrySet()) {
            String key = entry.getKey();
            Copy copy = entry.getValue();
            Preconditions.checkNotNull(key, "Media copy key cannot be null");
            Preconditions.checkNotNull(copy, "Media copy value cannot be null");
            Preconditions.checkArgument(!key.isEmpty(), "Media copy key cannot be empty");

            //  key must match the copy's barcode
            Preconditions.checkState(
                    key.equals(copy.getBarcode()),
                    "Media copy key must match copy barcode"
            );
        }

        // Waitlist
        for (Member member : waitlist) {
            Preconditions.checkNotNull(member, "waitlist contains null member");
        }

        // Reviews
        for (Review review : reviews) {
            Preconditions.checkNotNull(review, "reviews list contains null review");
        }
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public MediaType getKind() { return kind; }
    public MediaCategory getCategory() { return category; }
    public Coordinate getCoordinate() { return location; }

    /**
     * Provides a read-only collection view of all copies associated with this media item.
     *
     * @return an unmodifiable collection of {@link Copy} objects.
     */
    public Collection<Copy> getCopies() {
        return Collections.unmodifiableCollection(mediaCopies.values());
    }

    /**
     * Provides a copy of the current waitlist queue.
     *
     * @return a new {@link ArrayDeque} containing the members on the waitlist (first-in-first-out).
     */
    public Deque<Member> getWaitlist() {
        return new ArrayDeque<>(waitlist);
    }

    /**
     * Provides a read-only list of all reviews submitted for this media item.
     *
     * @return an unmodifiable list of {@link Review} objects.
     */
    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    // Copy management
    /**
     * Adds a physical copy to this media item if its barcode is not already present.
     * Invariants are checked after insertion.
     *
     * @param copy the {@link Copy} object to add. Must not be {@code null}.
     */
    public void addCopy(Copy copy) {
        Preconditions.checkNotNull(copy, "copy must not be null");
        String key = copy.getBarcode();

        if (!mediaCopies.containsKey(key)) {
            mediaCopies.put(key, copy);
            checkMedia();
        }
    }

    /**
     * Adds a new review from the given member after validating input.
     * Uses the {@link Review.ReviewBuilder} for strict domain validation.
     *
     * @param memberId  ID of the member leaving the review (must be &gt; 0).
     * @param rating    rating (must be between 1 and 5).
     * @param reviewText non-blank text with at least one non-space character.
     * @throws InvalidReviewException if any review property (ID, rating, text) is invalid.
     */
    public void addReview(int memberId, int rating, String reviewText) throws InvalidReviewException {
        // Preconditions for immediate feedback based on parameters
        Preconditions.checkArgument(memberId > 0, "memberId must be > 0");
        Preconditions.checkNotNull(reviewText, "reviewText cannot be null");
        Preconditions.checkArgument(!reviewText.isEmpty(), "reviewText cannot be blank");
        Preconditions.checkArgument(rating >= 1 && rating <= 5,
                "rating must be between 1 and 5");

        Review review = new Review.ReviewBuilder()
                .memberId(memberId)
                .rating(rating)
                .text(reviewText)
                .build();

        reviews.add(review);
        checkMedia();
    }


    /**
     * Adds a member to the end of the waitlist queue.
     * Preconditions ensure the member is not {@code null}.
     *
     * @param member the {@link Member} joining the waitlist. Must not be {@code null}.
     */
    public void joinWaitlist(Member member) {
        checkMedia(); // pre
        Preconditions.checkNotNull(member, "member must not be null");
        int oldSize = waitlist.size();
        waitlist.addLast(member);
        checkMedia(); // post
        Preconditions.checkState(waitlist.size() == oldSize + 1,
                "waitlist size must increase by one after join");
    }

    /**
     * Removes and returns the member at the front of the waitlist (FIFO).
     * Returns {@code null} if the waitlist is empty.
     *
     * @return the next {@link Member} waiting, or {@code null}.
     */
    public Member pollNextFromWaitlist() {
        checkMedia(); // pre
        return waitlist.pollFirst();
    }

    /**
     * Checks if the waitlist queue is currently non-empty.
     *
     * @return {@code true} if there is at least one member waiting; {@code false} otherwise.
     */
    public boolean hasWaitlist() {
        return !waitlist.isEmpty();
    }
}