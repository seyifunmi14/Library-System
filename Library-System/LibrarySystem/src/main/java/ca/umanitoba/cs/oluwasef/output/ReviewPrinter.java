package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.Review;
import java.util.List;

/**
 * A review printer that formats {@link Review} information for display.
 * Matches UML design where ReviewPrinter has an instance variable
 * referring to the reviews it prints (composition).
 */
public final class ReviewPrinter {
    private final String itemName;
    private final List<Review> reviews;

    /**
     * Constructs a ReviewPrinter for a given item.
     *
     * @param itemName the name or title of the media/resource being reviewed
     * @param reviews  the list of reviews to print
     */
    public ReviewPrinter(String itemName, List<Review> reviews) {
        this.itemName = itemName;
        this.reviews = reviews;
    }


    /**
     * Prints the reviews for this printer’s item.
     */
    public void print() {
        if (reviews == null || reviews.isEmpty()) {
            System.out.println("No reviews for '" + itemName + "' yet.");
        } else {
            System.out.println("Reviews for '" + itemName + "':");
            int i = 1;
            for (Review review : reviews) {
                System.out.println(i++ + ") " + review.rating() + "/5 by "
                        + review.memberId());
                System.out.println("   " + review.text());
                System.out.println();
            }
        }
    }
}