package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.domain.*;
import ca.umanitoba.cs.oluwasef.exceptions.BorrowingException;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidReviewException;
import ca.umanitoba.cs.oluwasef.exceptions.UserCancelledException;
import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import ca.umanitoba.cs.oluwasef.logic.MediaManager;
import ca.umanitoba.cs.oluwasef.output.LibraryPrinter;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.*;

/**
 * UI for all media-related actions:
 */
public final class MediaDisplay {

    private final Scanner keyboard;
    private final MemberManager memberManager;
    private final MediaManager mediaManager;

    /**
     * Creates a new MediaDisplay object.
     * @param keyboard      scanner for reading user input
     * @param memberManager logic component that manages the current logged-in member
     * @param mediaManager  logic component that manages borrow/return operations
     */
    public MediaDisplay(Scanner keyboard,
                        MemberManager memberManager,
                        MediaManager mediaManager) {
        this.keyboard = Preconditions.checkNotNull(keyboard);
        this.memberManager = Preconditions.checkNotNull(memberManager);
        this.mediaManager = Preconditions.checkNotNull(mediaManager);
    }

    /**
     * Show a paged table of media for the given library and let the user select one.
     * @param keyboard scanner used for input
     * @param library  the library whose media are being listed
     * @return the chosen {@link Media}, or {@code null} if the user quits
     */
    public static Media chooseMediaFromLibrary(Scanner keyboard, Library library) {
        List<Media> mediaList = new ArrayList<>(library.getMedia());
        if (mediaList.isEmpty()) {
            System.out.println("No media available in this library.");
            return null;
        }

        final int pageSize = 5;
        int page = 0;

        while (true) {
            int from = page * pageSize;
            if (from >= mediaList.size()) {
                page = 0;
                from = 0;
            }
            int to = Math.min(from + pageSize, mediaList.size());

            System.out.println();
            System.out.println("--- Media (" + (from + 1) + "-" + to + " of " + mediaList.size() + ") ---");
            System.out.printf("Idx  %-25s %-18s %-10s %-10s  %s%n",
                    "Title", "Creator", "Type", "Category", "Avail/Total");
            System.out.println("---------------------------------------------------------------------");

            for (int i = from; i < to; i++) {
                Media m = mediaList.get(i);

                int totalCopies = m.getCopies().size();
                long available = m.getCopies()
                        .stream()
                        .filter(c -> c.getStatus() == CopyStatus.AVAILABLE)
                        .count();

                System.out.printf("%3d) %-25s %-18s %-10s %-10s  %2d/%2d%n",
                        i + 1,
                        truncate(m.getTitle(), 25),
                        truncate(m.getCreator(), 18),
                        m.getKind(),
                        m.getCategory(),
                        available,
                        totalCopies);
            }

            System.out.println("---------------------------------------------------------------------");
            System.out.print("[N]ext page, [P]revious page, or [Q]uit: ");
            String input = keyboard.nextLine().trim().toLowerCase();

            switch (input) {
                case "q" -> {
                    return null;
                }
                case "n" -> {
                    if ((page + 1) * pageSize < mediaList.size()) {
                        page++;
                    } else {
                        System.out.println("Already on last page.");
                    }
                }
                case "p" -> {
                    if (page > 0) {
                        page--;
                    } else {
                        System.out.println("Already on first page.");
                    }
                }
                default -> {
                    try {
                        int choice = Integer.parseInt(input);
                        if (choice < 1 || choice > mediaList.size()) {
                            System.out.println("Please enter a number between 1 and " + mediaList.size() + ".");
                        } else {
                            return mediaList.get(choice - 1);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter N, P, Q, or a media number.");
                    }
                }
            }
        }
    }

    /**
     * Small helper for neat, fixed-width column printing.
     * @param s   the original string
     * @param max maximum length allowed
     * @return truncated string with an ellipsis if necessary, or empty string if {@code s} is null
     */
    private static String truncate(String s, int max) {
        String result = "";
        if (s != null) {
            if (s.length() <= max) {
                result = s;
            } else {
                result = s.substring(0, Math.max(1, max - 1)) + "…";
            }
        }
        return result;
    }

    /**
     * Main media menu loop.
     */
    public void run() {
        String choice;

        do {
            System.out.println();
            System.out.println("--- Media ---");
            System.out.println("1) Borrow media");
            System.out.println("2) Return media (read/write review)");
            System.out.println("3) Show media reviews");
            System.out.println("4) Back");
            System.out.print("Choose an option: ");

            choice = keyboard.nextLine().trim();

            switch (choice) {
                case "1" -> borrowFlow();
                case "2" -> returnFlow();
                case "3" -> showReviewsFlow();
                case "4" -> { /* back to home */ }
                default -> System.out.println("Unknown option.");
            }
        } while (!"4".equals(choice));
    }

    /**
     * Handles borrowing a media item:
     * Ensures a member is logged in.
     * Lets the user pick a library.
     * Lets the user pick a media item from that library.
     * Calls {@link MediaManager#borrowMedia(Library, String, LocalDate)}.
     * Prints user-friendly messages for all relevant exceptions.
     *
     */
    private void borrowFlow() {
        if (!requireLogin()) {
            System.out.println("Please log in first");
            return;
        }
        LibrarySystem system = memberManager.getSystem();
        try {
            Library library = pickLibrary(system);
            if (library == null) {
                System.out.println("Action cancelled: No library was selected.");
                return;
            }

            Media media = chooseMediaFromLibrary(keyboard, library);
            if (media == null) {
                System.out.println("Action cancelled: No media was selected.");
                return;
            }

            mediaManager.borrowMedia(
                    library,
                    media.getId().toString(),
                    LocalDate.now()
            );
            System.out.println("Successfully borrowed: " + media.getTitle());
        } catch (UserCancelledException e) {
            System.out.println("Action cancelled. Returning to Media menu.");
        } catch (BorrowingException e) {
            System.out.println(
                    "You cannot borrow this item right now because you have overdue items. " +
                            "Please return them before borrowing more media."
            );
        } catch (EntityNotFoundException e) {
            System.out.println(
                    "That media item could not be found in this library. " +
                            "Please try selecting it again."
            );
        }
    }


    /**
     * Handles returning a borrowed media copy and optionally leaving a review.
     * Ensures a member is logged in.
     * Lets the user pick a library.
     * Lists all items the current member has borrowed from that library.
     * Prompts which one to return (0 to cancel).
     * Optionally asks for a rating and review text.
     * Calls {@link MediaManager#returnMedia(Library, String, String, LocalDate, Integer, String)}.
     */
    private void returnFlow() {
        LibrarySystem system = memberManager.getSystem();
        try {

            Member current = memberManager.getCurrentUser();
            if (!requireLogin()) {
                System.out.println("Please Log in first");
                return;
            }

            Library library = pickLibrary(system);
            if (library == null) {
                System.out.println("Action cancelled: No library was selected.");
                return;
            }
            // Find only items this member has borrowed from this library
            List<MediaManager.BorrowedItem> borrowed =
                    mediaManager.findBorrowedItemsForMember(library, current);

            if (borrowed.isEmpty()) {
                System.out.println("You have no borrowed items in this library.");
                return;
            }

            // Display a compact table of their loans
            System.out.println();
            System.out.println("--- Your borrowed media ---");
            System.out.printf("%-4s %-30s %-15s %-12s%n",
                    "#", "Title", "Copy barcode", "Due date");
            System.out.println("---------------------------------------------------------------");

            for (int i = 0; i < borrowed.size(); i++) {
                MediaManager.BorrowedItem item = borrowed.get(i);
                Media m = item.getMedia();
                Copy c = item.getCopy();

                String dueText;
                java.time.LocalDate due = c.getDueDate();
                if (due == null) {
                    dueText = "n/a";
                } else {
                    dueText = due.toString();
                }

                System.out.printf("%-4d %-30s %-15s %-12s%n",
                        (i + 1),
                        truncate(m.getTitle(), 30),
                        c.getBarcode(),
                        dueText);
            }

            System.out.println("---------------------------------------------------------------");
            System.out.print("Choose an item to return (0 to cancel): ");

            int choice;
            while (true) {
                String raw = keyboard.nextLine().trim();
                try {
                    choice = Integer.parseInt(raw);
                    if (choice == 0) {
                        System.out.println("Return cancelled.");
                        return;
                    }
                    if (choice < 1 || choice > borrowed.size()) {
                        System.out.println("Please enter a number between 1 and " + borrowed.size() + ", or 0 to cancel.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter digits only (0 to cancel).");
                }
            }

            // Pick the chosen borrowed item
            MediaManager.BorrowedItem selected = borrowed.get(choice - 1);
            Media media = selected.getMedia();
            Copy copy = selected.getCopy();

            Integer rating = null;
            String reviewText = null;

            // 1. Ask user if they want to review
            String ans;
            do {
                System.out.print("Leave a review? (y/n): ");
                ans = keyboard.nextLine().trim().toLowerCase();
                if (!ans.equals("y") && !ans.equals("n")) {
                    System.out.println("Please enter only 'y' or 'n'.");
                }
            } while (!ans.equals("y") && !ans.equals("n"));

            // 2. If yes, collect rating and text with validation helpers
            if (ans.equals("y")) {
                rating = getRatingInput();
                reviewText = getTextInput();
            }

            // 3. Return the media (logic layer decides what to do with rating/review)
            mediaManager.returnMedia(
                    library,
                    media.getId().toString(),
                    copy.getBarcode(),
                    java.time.LocalDate.now(),
                    rating,
                    reviewText
            );

            System.out.println("Returned successfully.");
        } catch (InvalidReviewException e){
            System.out.println("Your Review was invalid( rating must be 1-5 and text must not be blank).");
        } catch (EntityNotFoundException | BorrowingException e) {
            System.out.println("Error! Invalid text");
        } catch (UserCancelledException e) {
            System.out.println("Return cancelled. Going back to Media menu.");
        }
    }

    /**
     * Handles showing all reviews for a selected media item in a chosen library.
     * If the media has no reviews, a short message is printed.
     */
    private void showReviewsFlow() {
        LibrarySystem system = memberManager.getSystem();
        try {
            Library library = pickLibrary(system);
            if (library == null) {
                System.out.println("Action cancelled: No library was selected.");
                return;
            }

            Media media = pickMedia(library);
            if (media == null) {
                System.out.println("Action cancelled: No media was selected.");
                return;
            }

            List<Review> reviews = media.getReviews();
            if (reviews.isEmpty()) {
                System.out.println("This media has no reviews yet.");
            } else {
                System.out.println();
                System.out.println("--- Reviews for " + media.getTitle() + " ---");
                for (Review r : reviews) {
                    System.out.println("* " + r.rating() + "/5 by " +
                            r.memberId() + ": " + r.text());
                }
            }
        } catch (UserCancelledException e) {
            System.out.println("Action cancelled. Returning to Media menu.");
        }
    }

    /**
     * Prompts the user to choose a library from the system.
     * Allows cancel by typing 'c', which throws {@link UserCancelledException}
     * and is caught by the caller.
     * @param system the current library system
     * @return the chosen {@link Library}, or {@code null} if none exist
     * @throws UserCancelledException if user enters 'c'
     */
    private Library pickLibrary(LibrarySystem system) throws UserCancelledException {
        List<Library> list = new ArrayList<>(system.getLibraries().values());
        if (list.isEmpty()) {
            System.out.println("No libraries available.");
            return null;
        }

        System.out.println("--- Libraries ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.print((i + 1) + ") ");
            new LibraryPrinter(list.get(i)).print();
        }

        System.out.print("Choose a library (or 'c' to cancel): ");
        int idx = readIntInRange(list.size());
        return list.get(idx - 1);
    }

    /**
     * Prompts the user to choose a media item from a library.
     * Supports paging through the media list.
     * @param library the library whose media are shown
     * @return the chosen {@link Media}, or {@code null} if none exist
     * @throws UserCancelledException if user enters 'c' during paging
     */
    private Media pickMedia(Library library) throws UserCancelledException {
        List<Media> list = new ArrayList<>(library.getMedia());
        if (list.isEmpty()) {
            System.out.println("No media available.");
            return null;
        }

        final int pageSize = 4; // items per page
        int page = 0;
        int total = list.size();
        int totalPages = (int) Math.ceil(total / (double) pageSize);

        while (true) {
            int start = page * pageSize;
            int end = Math.min(start + pageSize, total);

            System.out.println();
            System.out.println("=== Media (page " + (page + 1) + " of " + totalPages + ") ===");
            System.out.printf("%-4s %-28s %-18s %-10s %-10s %-10s%n",
                    "#", "Title", "Creator", "Type", "Category", "Copies");
            System.out.println("-------------------------------------------------------------------------------");

            for (int i = start; i < end; i++) {
                Media m = list.get(i);

                Collection<Copy> copies = m.getCopies();
                int totalCopies = copies.size();
                int availableCopies = 0;
                for (Copy c : copies) {
                    if (c.isAvailable()) {
                        availableCopies++;
                    }
                }

                int displayNumber = i + 1;
                System.out.printf("%-4d %-28s %-18s %-10s %-10s %-10s%n",
                        displayNumber,
                        truncate(m.getTitle(), 28),
                        truncate(m.getCreator(), 18),
                        m.getKind(),
                        m.getCategory(),
                        availableCopies + " / " + totalCopies);
            }

            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("Enter a media number, or:");
            System.out.println("  n = next page   p = previous page   c = cancel");
            System.out.print("Choice: ");

            String input = keyboard.nextLine().trim().toLowerCase();

            switch (input) {
                case "c" -> throw new UserCancelledException("User cancelled media selection.");
                case "n" -> {
                    if (page + 1 < totalPages) {
                        page++;
                    } else {
                        System.out.println("Already on the last page.");
                    }
                    continue;
                }
                case "p" -> {
                    if (page > 0) {
                        page--;
                    } else {
                        System.out.println("Already on the first page.");
                    }
                    continue;
                }
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > total) {
                    System.out.println("Please enter a number between 1 and " + total + ".");
                    continue;
                }
                return list.get(choice - 1);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number, or n / p / c.");
            }
        }
    }


    /**
     * Prompts the user for a rating between 1 and 5 (inclusive).
     * Loops until a valid integer is entered.
     * @return the chosen rating
     */
    private Integer getRatingInput() {
        Integer rating = null;

        while (rating == null) {
            System.out.print("Rating (1–5): ");
            String raw = keyboard.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value < 1 || value > 5) {
                    System.out.println("Rating must be between 1 and 5.");
                } else {
                    rating = value; // valid → exit loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter digits only (1–5).");
            }
        }
        return rating;
    }

    /**
     * Prompts the user for review text:
     * Cannot be blank.
     * Must contain at least one letter (prevents “1234” or “!!!”).
     * @return the validated review text
     */
    private String getTextInput() {
        String text = null;

        while (text == null) {
            System.out.print("Review text: ");
            String raw = keyboard.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("Review cannot be blank. Please enter some text.");
                continue;
            }
            // Must contain at least one letter A–Z or a–z
            if (!raw.matches(".*[A-Za-z].*")) {
                System.out.println("Review must contain at least one letter.");
                continue;
            }
            text = raw;
        }
        return text;
    }

    /**
     * Reads a line and trims whitespace.
     * If the user types just "c" (in any case), a {@link UserCancelledException} is thrown.
     * @return the line of input if not cancelled
     * @throws UserCancelledException if the user typed "c"
     */
    private String readLineOrCancel() throws UserCancelledException {
        String raw = keyboard.nextLine().trim();
        if (raw.equalsIgnoreCase("c")) {
            throw new UserCancelledException("User cancelled.");
        }
        return raw;
    }

    /**
     * Reads an integer in the range [1, max], or lets the caller handle cancellation.
     * Uses {@link #readLineOrCancel()} so the user can type 'c' to cancel.
     * @param max maximum allowed value
     * @return a valid integer between 1 and max
     */
    private int readIntInRange(int max) {
        int value;
        do {
            try {
                String raw = readLineOrCancel();
                value = Integer.parseInt(raw);

                if (value < 1 || value > max) {
                    System.out.println("Enter a number between 1 and " + max + ".");
                    value = -1;
                }
            } catch (NumberFormatException | UserCancelledException e) {
                System.out.println("Please enter digits only, or 'c' to cancel");
                value = -1;
            }
        } while (value == -1);
        return value;
    }

    /**
     * Checks if a member is currently logged in.
     * @return {@code true} if logged in, {@code false} otherwise
     */
    private boolean requireLogin() {
        if (!memberManager.isLoggedIn()) {
            System.out.println("You must be logged in to do that.");
            return false;
        }
        return true;
    }
}