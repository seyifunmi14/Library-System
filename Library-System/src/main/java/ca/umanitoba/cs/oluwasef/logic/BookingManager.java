package ca.umanitoba.cs.oluwasef.logic;

import ca.umanitoba.cs.oluwasef.domain.Booking;
import ca.umanitoba.cs.oluwasef.domain.Library;
import ca.umanitoba.cs.oluwasef.domain.Member;
import ca.umanitoba.cs.oluwasef.domain.Resource;
import ca.umanitoba.cs.oluwasef.exceptions.BookingConflictException;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidBookingException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidInputException;
import com.google.common.base.Preconditions;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Logic layer for resource bookings.
 */
public final class BookingManager {

    private static final LocalTime OPEN_TIME  = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(20, 0);
    private static final int SLOT_MINUTES     = 60;

    private final MemberManager memberManager;

    /**
     * Initializes the booking manager, requiring a member manager to handle user context.
     *
     * @param memberManager the manager responsible for accessing the current user. Must not be {@code null}.
     */
    public BookingManager(MemberManager memberManager) {
        this.memberManager = Preconditions.checkNotNull(memberManager, "memberManager must not be null");
    }

    public static LocalTime getOpenTime() {
        return OPEN_TIME;
    }

    public static LocalTime getCloseTime() {
        return CLOSE_TIME;
    }


    /**
     * Book a resource for the currently logged-in member.
     *
     * @param library    the library containing the resource.
     * @param resourceId the numeric ID of the resource.
     * @param start      booking start time.
     * @param end        booking end time (usually start + 60 minutes).
     * @throws InvalidInputException    if parameters are null, invalid, or no user logged in.
     * @throws EntityNotFoundException  if the resource cannot be found.
     * @throws BookingConflictException if the time slot is not free.
     * @throws InvalidBookingException  if the Booking builder detects invalid state.
     */
    public void bookResource(Library library,
                             int resourceId,
                             LocalDateTime start,
                             LocalDateTime end)
            throws InvalidInputException,
            EntityNotFoundException,
            BookingConflictException,
            InvalidBookingException {

        if (library == null || start == null || end == null) {
            throw new InvalidInputException();
        }
        if (resourceId <= 0) {
            throw new InvalidInputException();
        }

        Member member = memberManager.getCurrentUser();
        if (member == null) {
            throw new InvalidInputException();
        }

        // validate time window (open/close, on hour, start < end)
        checkBooking(start, end);
        Resource resource = library.requireResource(resourceId);

        // check availability
        if (!resource.isAvailableDuring(start, end)) {
            // the slot is not available -> conflict
            throw new BookingConflictException();
        }

        // compute next booking ID for this resource
        int nextBookingId = computeNextBookingId(resource);

        Booking booking = new Booking.BookingBuilder()
                .bookingId(nextBookingId)
                .resourceId(resource.getResourceId())
                .memberId(member.getMemberId())
                .start(start)
                .end(end)
                .build();
        resource.addBooking(booking);
    }

    /**
     * Return all bookings for a specific resource.
     *
     * @param library    the library containing the resource.
     * @param resourceId the numeric ID of the resource.
     * @return a list of all Booking objects associated with the resource.
     * @throws EntityNotFoundException if the resource cannot be found in the library.
     */
    public List<Booking> getBookingsForResource(Library library, int resourceId)
            throws EntityNotFoundException {

        if (library == null || resourceId <= 0) {
            throw new EntityNotFoundException();
        }

        Resource resource = library.requireResource(resourceId);
        // Resource stores bookings in a Map<Integer, Booking>
        Map<Integer, Booking> bookingMap = resource.getBookingList();
        return new ArrayList<>(bookingMap.values());
    }

    /**
     * Finds all free 1-hour slots for a single day.
     *
     * @param resource the resource to check availability for.
     * @param date     the specific date to check.
     * @return a list of LocalDateTime objects, where each object is the start time of an available 1-hour slot.
     */
    public List<LocalDateTime> availableSlotsPerDay(Resource resource, LocalDate date) {
        List<LocalDateTime> freeSlots = new ArrayList<>();

        if (resource == null || date == null) {
            return freeSlots;
        }

        LocalTime startTime = OPEN_TIME;
        while (!startTime.plusMinutes(SLOT_MINUTES).isAfter(CLOSE_TIME)) {
            LocalDateTime start = date.atTime(startTime);
            LocalDateTime end   = start.plusMinutes(SLOT_MINUTES);

            if (resource.isAvailableDuring(start, end)) {
                freeSlots.add(start);
            }

            startTime = startTime.plusMinutes(SLOT_MINUTES);
        }

        return freeSlots;
    }

    /**
     * Computes a map of free 1-hour slot start times, keyed by date,
     * over a specified date range.
     *
     * @param resource  the resource to check availability for.
     * @param startDate the start date (inclusive) of the range.
     * @param endDate   the end date (inclusive) of the range.
     * @return a Map where the key is the LocalDate and the value is a list of available LocalTime slot start times.
     */
    public Map<LocalDate, List<LocalTime>> rangeOfDatesSlots(Resource resource,
                                                             LocalDate startDate,
                                                             LocalDate endDate) {
        Map<LocalDate, List<LocalTime>> freeSlots = new LinkedHashMap<>();

        if (resource == null || startDate == null || endDate == null) {
            return freeSlots;
        }
        if (endDate.isBefore(startDate)) {
            return freeSlots;
        }

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<LocalTime> daySlots = new ArrayList<>();

            LocalTime currentTime = OPEN_TIME; // Renamed 't' to 'currentTime'
            while (!currentTime.plusMinutes(SLOT_MINUTES).isAfter(CLOSE_TIME)) {
                LocalDateTime start = current.atTime(currentTime);
                LocalDateTime end   = start.plusMinutes(SLOT_MINUTES);

                if (resource.isAvailableDuring(start, end)) {
                    daySlots.add(currentTime);
                }

                currentTime = currentTime.plusMinutes(SLOT_MINUTES);
            }
            freeSlots.put(current, daySlots);
            current = current.plusDays(1);
        }

        return freeSlots;
    }

    /**
     * Performs validation checks on a booking's start and end times, ensuring they are valid
     * (e.g., within operating hours, on the hour, start is before end).
     *
     * @param start the start time of the booking.
     * @param end   the end time of the booking.
     * @throws InvalidInputException if the times are null, not on the hour, outside operating hours, or if start is not before end.
     */
    private static void checkBooking(LocalDateTime start, LocalDateTime end)
            throws InvalidInputException {

        if (start == null || end == null) {
            throw new InvalidInputException();
        }
        // must be strictly before
        if (!start.isBefore(end)) {
            throw new InvalidInputException();
        }
        LocalTime startTime = start.toLocalTime(); // Renamed 's' to 'startTime'
        LocalTime endTime = end.toLocalTime();     // Renamed 'e' to 'endTime'

        // slots must be on the hour
        if (startTime.getMinute() != 0) {
            throw new InvalidInputException();
        }
        if (endTime.getMinute() != 0){
            throw new InvalidInputException();
        }
        // within opening hours
        if (startTime.isBefore(OPEN_TIME)) {
            throw new InvalidInputException();
        }
        if (endTime.isAfter(CLOSE_TIME)) {
            throw new InvalidInputException();
        }
    }

    /**
     * Next booking ID for this resource (1 if none, otherwise max+1).
     *
     * @param resource the resource to check the existing bookings for.
     * @return the next available integer ID for a new booking.
     */
    private int computeNextBookingId(Resource resource) {
        Map<Integer, Booking> map = resource.getBookingList();
        if (map.isEmpty()) {
            return 1;
        }
        return Collections.max(map.keySet()) + 1;
    }

    /**
     * Creates a LocalDate object after validating the year range.
     *
     * @param year  the year component. Must be between 2025 and 2026 (inclusive).
     * @param month the month component (1-12).
     * @param day   the day component (1-31).
     * @return a valid LocalDate object.
     * @throws InvalidInputException if the year is out of range or the date components form an invalid calendar date.
     */
    public LocalDate buildBookingDate(int year, int month, int day) throws InvalidInputException {
        // year range rule
        if (year < 2025 || year > 2026) {
            throw new InvalidInputException();
        }

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            // covers invalid month/day combos, leap year, etc.
            throw new InvalidInputException();
        }
    }

    /**
     * Parses and validates a start time string for a 1-hour booking.
     * Enforces:
     * - format HH:MM
     * - minutes = 00
     * - slot within [open, close]
     *
     * @param input the time string to parse, e.g., "09:00".
     * @return a valid LocalTime object that is on the hour and within operating hours.
     * @throws InvalidInputException if the input string is incorrectly formatted, the minutes are not zero, or the resulting slot is outside operating hours.
     */
    public LocalTime buildBookingTime(String input) throws InvalidInputException {
        try {
            LocalTime time = LocalTime.parse(input); // may throw DateTimeParseException

            if (time.getMinute() != 0) {
                throw new InvalidInputException();
            }

            LocalTime open  = getOpenTime();
            LocalTime close = getCloseTime();
            LocalTime end   = time.plusHours(1);

            if (time.isBefore(open) || end.isAfter(close)) {
                throw new InvalidInputException();
            }
            return time;
        } catch (DateTimeParseException e) {
            throw new InvalidInputException();
        }
    }
}