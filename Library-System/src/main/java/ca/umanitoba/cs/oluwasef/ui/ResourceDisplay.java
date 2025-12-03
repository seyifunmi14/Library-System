package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.domain.Booking;
import ca.umanitoba.cs.oluwasef.domain.Library;
import ca.umanitoba.cs.oluwasef.domain.LibrarySystem;
import ca.umanitoba.cs.oluwasef.domain.Resource;
import ca.umanitoba.cs.oluwasef.exceptions.BookingConflictException;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidBookingException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidInputException;
import ca.umanitoba.cs.oluwasef.logic.BookingManager;
import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * This class handles all UI interactions related to booking and viewing
 * library resources (e.g. study rooms, computers).
 */
public final class ResourceDisplay {

    private final Scanner keyboard;
    private final MemberManager memberManager;
    private final BookingManager bookingManager;

    /**
     * Constructs a new ResourceDisplay UI.
     * @param keyboard       scanner used to read input from the user
     * @param memberManager  logic component that tracks the logged-in member
     * @param bookingManager logic component that manages resource bookings
     */
    public ResourceDisplay(Scanner keyboard,
                           MemberManager memberManager,
                           BookingManager bookingManager) {
        this.keyboard = Preconditions.checkNotNull(keyboard);
        this.memberManager = Preconditions.checkNotNull(memberManager);
        this.bookingManager = Preconditions.checkNotNull(bookingManager);
    }

    /**
     * Main entry point for the resource menu.
     * Loops until the user chooses to go back.
     */
    public void run() {
        String choice;

        do {
            System.out.println();
            System.out.println("--- Resources ---");
            System.out.println("1) Book a resource");
            System.out.println("2) View bookings");
            System.out.println("3) Back");
            System.out.print("Choose: ");

            choice = keyboard.nextLine().trim();
            switch (choice) {
                case "1" -> bookFlow();
                case "2" -> viewBookingsFlow();
                case "3" -> { /* back */ }
                default -> System.out.println("Unknown option: " + choice);
            }
        } while (!"3".equals(choice));
    }


    /**
     * Handles the full flow of booking a resource:
     */
    private void bookFlow() {
        if (!requireLogin()) {
            System.out.println("You must be logged in to book a resource.");
            return;
        }

        LibrarySystem system = memberManager.getSystem();
        Library library = pickLibrary(system);
        if (library == null) {
            System.out.println("Booking cancelled: no library selected.");
            return;
        }

        Resource resource = pickResource(library);
        if (resource == null) {
            System.out.println("Booking cancelled: no resource selected.");
            return;
        }

        // Let the user view availability (TD / ROD) and choose a booking date
        LocalDate bookingDate = handleShowResourceBooking(resource);
        if (bookingDate == null) {
            System.out.println("No booking made. Returning to menu.");
            return;
        }

        // Now only ask for time on that chosen date
        LocalTime startTime = promptBookingTime();
        LocalDateTime start = bookingDate.atTime(startTime);
        LocalDateTime end   = start.plusMinutes(60);

        try {
            bookingManager.bookResource(
                    library,
                    resource.getResourceId(),
                    start,
                    end
            );
            System.out.println("Booking confirmed! From: " + start + " to " + end);

        } catch (InvalidInputException e) {
            System.out.println(
                    "Invalid date/time. " +
                            "Start must be before end, within years 2025–2026, " +
                            "and between " + BookingManager.getOpenTime() +
                            " and " + BookingManager.getCloseTime() + "."
            );
        } catch (BookingConflictException e) {
            // Time slot already taken by another booking
            System.out.println(
                    "That time slot is already booked. " +
                            "Please choose another available time."
            );
        } catch (EntityNotFoundException e) {
            // Resource lookup failed in the system
            System.out.println("That resource could not be found in this library.");
        } catch (InvalidBookingException e) {
            // Booking failed validation in the domain/logic layer
            System.out.println("Your booking could not be created. " +
                                     "Please ensure the timeslot is valid and try again.");
        }
    }

    /**
     * Allows the user to view availability for a resource and pick a booking date.
     * @param r the resource whose availability is being shown
     * @return the date the user wants to book, or {@code null} on cancel
     */
    private LocalDate handleShowResourceBooking(Resource r) {
        while (true) {
            System.out.println();
            System.out.println("For your booking, do you want to view availability by:");
            System.out.println("  TD  - timeslots for a single day");
            System.out.println("  ROD - range of dates");
            System.out.println("  C   - cancel");
            System.out.print("Enter 'TD', 'ROD', or 'C': ");

            String input = keyboard.nextLine().trim().toUpperCase();

            switch (input) {
                case "TD" -> {
                    // Show availability for a single date
                    LocalDate date = promptBookingDate();
                    java.util.List<LocalDateTime> daySlots =
                            bookingManager.availableSlotsPerDay(r, date);
                    printDaySlots(r, date, daySlots);
                    return date;
                }

                case "ROD" -> {
                    // Show availability for a range of dates and let them pick one
                    System.out.println("Enter START date.");
                    LocalDate start = promptBookingDate();
                    System.out.println("Enter END date.");
                    LocalDate end = promptBookingDate();

                    Map<LocalDate, java.util.List<LocalTime>> rangeSlots =
                            bookingManager.rangeOfDatesSlots(r, start, end);
                    printRangeSlots(r, rangeSlots);

                    // Force the user to pick a date within the range
                    while (true) {
                        System.out.println("Enter the exact date (within that range) you want to book:");
                        LocalDate bookingDate = promptBookingDate();
                        boolean inRange =
                                (bookingDate.isEqual(start) || bookingDate.isAfter(start)) &&
                                        (bookingDate.isEqual(end)   || bookingDate.isBefore(end));
                        if (inRange) {
                            return bookingDate;
                        } else {
                            System.out.println("Invalid date! Your booking date must be between "
                                    + start + " and " + end + ". Please try again.");
                        }
                    }
                }
                case "C" -> {
                    // User cancels the booking flow
                    System.out.println("Booking cancelled.");
                    return null;
                }

                default -> System.out.println("Invalid command! Please enter 'TD', 'ROD' or 'C'.");
            }
        }
    }

    /**
     * Handles viewing all bookings for a particular resource.
     */
    private void viewBookingsFlow() {
        LibrarySystem system = memberManager.getSystem();
        Library library = pickLibrary(system);
        if (library == null) {
            System.out.println("No library selected.");
            return;
        }

        Resource resource = pickResource(library);
        if (resource == null) {
            System.out.println("No resource selected.");
            return;
        }

        try {
            java.util.List<Booking> bookings =
                    bookingManager.getBookingsForResource(library, resource.getResourceId());

            if (bookings.isEmpty()) {
                System.out.println("This resource has no bookings.");
                return;
            }

            System.out.println();
            System.out.println("--- Bookings for " + resource.getResourceName() + " ---");
            for (Booking b : bookings) {
                System.out.println("Booking #" + b.bookingId()
                        + " | member " + b.memberId()
                        + " | " + b.start() + " → " + b.end());
            }

        } catch (EntityNotFoundException e) {
            System.out.println("That resource could not be found in this library.");
        }
    }


    /**
     * Prompts for a booking date with validation:
     * @return the validated {@link LocalDate}
     */

    private LocalDate promptBookingDate() {
        while (true) {
            try {
                System.out.print("  Year (2025–2026): ");
                int year = Integer.parseInt(keyboard.nextLine().trim());
                System.out.print("  Month (1–12): ");
                int month = Integer.parseInt(keyboard.nextLine().trim());
                System.out.print("  Day: ");
                int day = Integer.parseInt(keyboard.nextLine().trim());
                return bookingManager.buildBookingDate(year, month, day);
            } catch (NumberFormatException e) {
                System.out.println("  -> Please enter digits only for year, month, and day.");
            } catch (InvalidInputException e) {
                System.out.println("  -> That date is not allowed. Please enter a valid date in 2025–2026.");
            }
        }
    }

    /**
     * Prompts the user for a 1-hour booking start time on the chosen date.
     * @return the validated start {@link LocalTime}
     */
    private LocalTime promptBookingTime() {
        while (true) {
            System.out.println();
            System.out.println("Enter preferred booking start time.");
            System.out.println("Format must be HH:MM with minute = 00 (e.g. 09:00, 13:00).");
            System.out.println("Library hours: " +
                    BookingManager.getOpenTime() + " to " + BookingManager.getCloseTime());
            System.out.print("Start time (HH:MM): ");

            String input = keyboard.nextLine().trim();

            try {
                return bookingManager.buildBookingTime(input);
            } catch (InvalidInputException e) {
                System.out.println(
                        "Invalid time! Make sure it is HH:MM, minute = 00, " +
                                "and the 1-hour slot fits inside opening hours."
                );
            }
        }
    }


    /**
     * Prints all available 1-hour slots for a given resource on a single day.
     * @param resource the resource being booked
     * @param date     the date we are displaying
     * @param slots    the list of LocalDateTime start times returned by the logic layer
     */
    private void printDaySlots(Resource resource,
                               LocalDate date,
                               java.util.List<LocalDateTime> slots) {
        System.out.println();
        System.out.println("Available 1-hour slots for " +
                resource.getResourceName() + " on " + date + ":");

        if (slots.isEmpty()) {
            System.out.println(" No free slots on this day.");
            return;
        }

        for (LocalDateTime dt : slots) {
            System.out.println("  - " + dt.toLocalTime() + " to " + dt.toLocalTime().plusHours(1));
        }
    }

    /**
     * Prints all available slots for a resource over a range of dates.
     * The data structure is produced by the logic layer:
     * each date maps to a list of LocalTime values representing free 1-hour slots.
     * @param resource    the resource being inspected
     * @param slotsByDate map from date → list of available start times
     */
    private void printRangeSlots(Resource resource,
                                 Map<LocalDate, java.util.List<LocalTime>> slotsByDate) {
        System.out.println();
        System.out.println("Availability for " + resource.getResourceName() + " over the selected range:");
        if (slotsByDate.isEmpty()) {
            System.out.println("  (No free slots in this range.)");
            return;
        }

        for (Map.Entry<LocalDate, java.util.List<LocalTime>> entry : slotsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            java.util.List<LocalTime> times = entry.getValue();

            System.out.print(date + ": ");
            if (times.isEmpty()) {
                System.out.println("(no free slots)");
            } else {
                StringBuilder sb = new StringBuilder();
                for (LocalTime t : times) {
                    if (sb.isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(t).append("–").append(t.plusHours(1));
                }
                System.out.println(sb);
            }
        }
    }


    /**
     * Prompts the user to choose a library from the system.
     * @param system the overall library system containing all libraries
     * @return the chosen {@link Library}, or {@code null} if none exist
     */
    private Library pickLibrary(LibrarySystem system) {
        List<Library> list = new ArrayList<>(system.getLibraries().values());
        if (list.isEmpty()) {
            System.out.println("No libraries available.");
            return null;
        }

        System.out.println("--- Libraries ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ") " + list.get(i).getName());
        }

        System.out.print("Choose library: ");
        int idx = readIntInRange(list.size());
        return list.get(idx - 1);
    }

    /**
     * Prompts the user to choose a resource from a library.
     * @param library the library whose resources will be listed
     * @return the chosen {@link Resource}, or {@code null} if none exist
     */
    private Resource pickResource(Library library) {
        List<Resource> list = new ArrayList<>(library.getResources().values());
        if (list.isEmpty()) {
            System.out.println("No resources.");
            return null;
        }

        System.out.println("--- Resources ---");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ") " + list.get(i).getResourceName());
        }

        System.out.print("Choose resource: ");
        int idx = readIntInRange(list.size());
        return list.get(idx - 1);
    }

    /**
     * Reads a whole number from 1 to {@code max} (inclusive) from the user.
     * Keeps prompting until the user enters a valid integer within range.
     * @param max the maximum allowed value
     * @return the validated integer in [1, max]
     */
    private int readIntInRange(int max) {
        int value;
        do {
            try {
                String raw = keyboard.nextLine().trim();
                value = Integer.parseInt(raw);

                if (value < 1 || value > max) {
                    System.out.println("Enter a whole number between " + 1 + " and " + max + ".");
                    value = -1;
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter digits only.");
                value = -1;
            }
        } while (value == -1);

        return value;
    }

    /**
     * Simple helper to check if a member is logged in before performing
     * actions that require authentication.
     * @return {@code true} if there is a logged-in member, {@code false} otherwise
     */
    private boolean requireLogin() {
        if (!memberManager.isLoggedIn()) {
            System.out.println("You must be logged in to do that.");
            return false;
        }
        return true;
    }
}