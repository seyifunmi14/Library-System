package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import ca.umanitoba.cs.oluwasef.logic.MediaManager;
import ca.umanitoba.cs.oluwasef.logic.BookingManager;
import ca.umanitoba.cs.oluwasef.logic.MapManager;
import ca.umanitoba.cs.oluwasef.output.MemberPrinter;
import com.google.common.base.Preconditions;

import java.util.Scanner;

/**
 * Top-level “home” menu the user sees after logging in.
 */
public final class LibraryHomeDisplay {

    private final Scanner keyboard;
    private final MemberManager memberManager;
    private final MediaManager mediaManager;
    private final BookingManager bookingManager;
    private final MapManager mapManager;

    /**
     * Creates a new LibraryHomeDisplay for the currently running session.
     *
     * @param keyboard       scanner used for all user input
     * @param memberManager  logic that tracks the currently logged-in member
     * @param mediaManager   logic for borrow/return operations
     * @param bookingManager logic for resource bookings
     * @param mapManager     logic for path-finding / map operations
     */
    public LibraryHomeDisplay(Scanner keyboard,
                              MemberManager memberManager,
                              MediaManager mediaManager,
                              BookingManager bookingManager,
                              MapManager mapManager) {

        this.keyboard = Preconditions.checkNotNull(keyboard);
        this.memberManager = Preconditions.checkNotNull(memberManager);
        this.mediaManager = Preconditions.checkNotNull(mediaManager);
        this.bookingManager = Preconditions.checkNotNull(bookingManager);
        this.mapManager = Preconditions.checkNotNull(mapManager);
    }

    /**
     * Main home menu loop.
     * Shows the currently logged-in member at the top and then offers:
     * Media (borrow/return)
     * Resources (booking)
     * Find route to media/resource
     * Logout and return to the previous screen
     * Each option simply constructs and runs the appropriate sub-display.
     * All domain logic (eligibility, conflicts, etc.) is handled in the logic layer.
     */
    public void run() {
        boolean keepGoing = true;

        while (keepGoing) {
            System.out.println();
            System.out.println("=== Library Home Menu ===");

            // Show the current member’s basic info
            new MemberPrinter(memberManager.getCurrentUser()).print();

            System.out.println("1) Media (borrow/return)");
            System.out.println("2) Resources (booking)");
            System.out.println("3) Find route to media/resource");
            System.out.println("4) Logout");
            System.out.print("Choose an option: ");

            String choice = keyboard.nextLine().trim();

            switch (choice) {
                case "1" -> new MediaDisplay(keyboard, memberManager, mediaManager).run();
                case "2" -> new ResourceDisplay(keyboard, memberManager, bookingManager).run();
                case "3" -> new FindRouteDisplay(keyboard, mapManager).run();
                case "4" -> {
                    // End the session at the logic layer and exit this menu.
                    memberManager.logout();
                    System.out.println("Logged out.");
                    keepGoing = false;
                }
                default -> System.out.println("Unknown option: " + choice);
            }
        }
    }
}