package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidBookingException;
import com.google.common.base.Preconditions;
import java.time.LocalDateTime;


/**
 * A resource booking. Bookings reserve {@link Resource} items for {@link Member} users at specific times.
 *
 * @param bookingId  The unique identifier for the booking. Must be greater than 0.
 * @param resourceId The ID of the {@link Resource} being booked. Must be greater than 0.
 * @param memberId   The ID of the {@link Member} making the booking. Must be greater than 0.
 * @param start      The date and time the booking period begins. Must not be null.
 * @param end        The date and time the booking period ends. Must not be null and must be after the start time.
 */
public record Booking(int bookingId, int resourceId, int memberId, LocalDateTime start, LocalDateTime end) {

    /**
     * Compact constructor – validates core invariants for the Booking record.
     */
    public Booking {
        Preconditions.checkArgument(bookingId > 0, "Booking ID must be greater than 0");
        Preconditions.checkArgument(resourceId > 0, "Resource ID must be greater than 0");
        Preconditions.checkArgument(memberId > 0, "Member ID must be greater than 0");
        Preconditions.checkNotNull(start, "Start Date cannot be null");
        Preconditions.checkNotNull(end, "End Date cannot be null");
        Preconditions.checkArgument(start.isBefore(end), "Start Date must be before End Date");

    }

    /**
     * Builder class for safely constructing and validating new {@link Booking} objects.
     */
    public static class BookingBuilder{
        private int bookingId;
        private int resourceId;
        private int memberId;
        private LocalDateTime start;
        private LocalDateTime end;

        /**
         * Creates an empty BookingBuilder instance.
         */
        public BookingBuilder() {}

        /**
         * Sets the unique booking ID.
         *
         * @param bookingId the ID. Must be greater than 0.
         * @return this builder instance.
         * @throws InvalidBookingException if the ID is less than or equal to zero.
         */
        public BookingBuilder bookingId(int bookingId) throws InvalidBookingException {
            if(bookingId <= 0){
                throw new InvalidBookingException();
            }
            this.bookingId = bookingId;
            return this;
        }

        /**
         * Sets the ID of the resource being booked.
         *
         * @param resourceId the resource ID. Must be greater than 0.
         * @return this builder instance.
         * @throws InvalidBookingException if the resource ID is less than or equal to zero.
         */
        public BookingBuilder resourceId(int resourceId) throws InvalidBookingException {
            if(resourceId <= 0){
                throw new InvalidBookingException();
            }
            this.resourceId = resourceId;
            return this;
        }

        /**
         * Sets the ID of the member making the booking.
         *
         * @param memberId the member ID. Must be greater than 0.
         * @return this builder instance.
         * @throws InvalidBookingException if the member ID is less than or equal to zero.
         */
        public BookingBuilder memberId(int memberId) throws InvalidBookingException {
            if(memberId <= 0){
                throw new InvalidBookingException();
            }
            this.memberId = memberId;
            return this;
        }

        /**
         * Sets the start date and time of the booking.
         *
         * @param start the start {@link LocalDateTime}. Must not be {@code null}.
         * @return this builder instance.
         * @throws InvalidBookingException if the start time is null.
         */
        public BookingBuilder start(LocalDateTime start) throws InvalidBookingException {
            if(start == null) {
                throw new InvalidBookingException();
            }
            this.start = start;
            return this;
        }

        /**
         * Sets the end date and time of the booking.
         *
         * @param end the end {@link LocalDateTime}. Must not be {@code null}.
         * @return this builder instance.
         * @throws InvalidBookingException if the end time is null.
         */
        public BookingBuilder end(LocalDateTime end) throws InvalidBookingException {
            if(end == null) {
                throw new InvalidBookingException();
            }
            this.end = end;
            return this;
        }
        /**
         * Builds and returns an immutable {@link Booking} object.
         * The Booking compact constructor will perform final invariant checks (e.g., start &lt; end).
         *
         * @return a new Booking instance with the accumulated properties.
         */
        public Booking build() {
            return new Booking(bookingId, resourceId, memberId, start, end);
        }
    }
}