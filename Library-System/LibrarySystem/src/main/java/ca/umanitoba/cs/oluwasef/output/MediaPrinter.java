package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.CopyStatus;
import ca.umanitoba.cs.oluwasef.domain.Media;


/**
 * Printer class that displays information about a {@link Media} item.
 * Contains an instance variable for the media being printed,
 * matching the UML domain model (MediaPrinter --o Media).
 */
public final class MediaPrinter {
    private final Media media;

    /**
     * Constructs a MediaPrinter for a given media.
     *
     * @param media the media object to print; must not be {@code null}.
     */
    public MediaPrinter(Media media) {
        this.media = media;
    }

    /**
     * Prints detailed information about the media.
     */
    public void print() {
        if (media == null) {
            System.out.println("Media not found.");
            return;
        }

        System.out.println("== MEDIA ==");
        System.out.println("Title: " + media.getTitle() + "  | Creator: " + media.getCreator());
        System.out.println("Kind: " + media.getKind() + " | Category: " + media.getCategory());

        long available = media.getCopies().stream()
                .filter(c -> c.getStatus() == CopyStatus.AVAILABLE)
                .count();

        System.out.println("Copies: " + media.getCopies().size() +
                " (available: " + available + ")");

        if (media.hasWaitlist()) {
            System.out.println("Waitlist size: " + media.getWaitlist().size());
        } else {
            System.out.println("Waitlist: empty");
        }

        if (!media.getReviews().isEmpty()) {
            System.out.println("Reviews:");
            media.getReviews().forEach(r -> {
                System.out.println("  - " + r.rating() + "/5 by " +
                        r.memberId() + ": " + r.text());
            });
        } else {
            System.out.println("No reviews yet.");
        }
    }
}