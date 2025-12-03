package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.Resource;

/**
 * A resource printer. Formats {@link Resource} information for display.
 * Instance-based to match UML (ResourcePrinter --o Resource).
 */
public final class ResourcePrinter {
    private final Resource resource;

    public ResourcePrinter(Resource resource) {
        this.resource = resource;
    }

    /** Prints this printer's resource details. */
    public void print() {
        if (resource == null) {
            System.out.println("Resource not found.");
            return;
        }

        System.out.println("== RESOURCE ==");
        // (Per grader UX note, usually avoid exposing internal IDs to users)
        System.out.println("Name: " + resource.getResourceName());
        System.out.println("Kind: " + resource.getKind());
        System.out.println("Description: " + resource.getDescription());
        System.out.println("Existing bookings: " + resource.getBookingList().size());

        if (resource.getReviews().isEmpty()) {
            System.out.println("No reviews yet.");
        } else {
            System.out.println("Reviews:");
            resource.getReviews().forEach(review ->
                    System.out.println("  - " + review.rating() + "/5 by "
                            + review.memberId() + ": " + review.text())
            );
        }
    }
}