package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidAccountException;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.*;

/**
 * A library member profile / account.
 * Members have login credentials (id + PIN) and a record of borrowed copies.
 */
public class Member {

    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String email;
    private final String pin;
    /** Whether this member is currently allowed to borrow new items. */
    private boolean canBorrow;

    /**
     * Tracks borrowed copies and their due dates.
     * Key is the copy barcode (or some logical identifier).
     */
    private final Map<String, LocalDate> borrowedCopies;

    /**
     * Private constructor used only by the {@link MemberBuilder}.
     */
    private Member(int memberId, String firstName, String lastName, String phoneNumber, String email, String pin, boolean canBorrow) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pin = pin;
        this.canBorrow = canBorrow;
        this.borrowedCopies = new LinkedHashMap<>();
        checkMember();
    }

    /**
     * Class invariants for Member.
     * Ensures all internal state (ID, names, contact info, PIN, loan counts) is valid.
     */
    private void checkMember() {
        Preconditions.checkArgument(memberId > 0 , "Member id must  be greater than 0");
        Preconditions.checkNotNull(firstName, "First Name cannot be null");
        Preconditions.checkState(!firstName.isBlank(), "First Name cannot be empty or blank");
        Preconditions.checkNotNull(lastName, "Last Name cannot be null");
        Preconditions.checkState(!lastName.isBlank(), "Last Name cannot be empty or blank");
        Preconditions.checkNotNull(email, "Email must not be null");
        Preconditions.checkState(!email.isBlank(), "Email must not be blank");
        Preconditions.checkState(email.contains("@"), "Email must contain '@'");

        Preconditions.checkNotNull(phoneNumber, "Phone number must not be null");
        Preconditions.checkState(!phoneNumber.isBlank(), "Phone number must not be blank");
        Preconditions.checkState(phoneNumber.matches("\\d{7,15}"),
                "Phone number must contain only digits and be between 7 and 15 digits long");

        Preconditions.checkNotNull(pin, "PIN must not be null");
        Preconditions.checkState(!pin.isBlank(), "PIN must not be blank");
        Preconditions.checkState(pin.matches("\\d{4}"), "PIN must be exactly 4 digits");
        Preconditions.checkState(borrowedCopies.size() <= LibrarySystem.getMaxActiveLoans(), "Member exceeds max active loans");
        Preconditions.checkNotNull(borrowedCopies, "Borrowed copies map must not be null");
        for (Map.Entry<String, LocalDate> entry : borrowedCopies.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Borrowed copy key must not be null");
            Preconditions.checkState(!entry.getKey().isBlank(), "Borrowed copy key must not be blank");

            LocalDate due = Preconditions.checkNotNull(entry.getValue(), "Borrowed copy due date must not be null");
            Preconditions.checkState(!due.isBefore(LocalDate.now()),
                    "Due date must not be before the current date");
        }
    }

    public int getMemberId() {
        return this.memberId;
    }

    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public String getFullName(){
        return firstName + " " + lastName;
    }


    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPin() {
        return this.pin;
    }

    public boolean canBorrow() {
        return canBorrow;
    }

    /**
     * Provides a read-only view of the currently borrowed copies map.
     * The map key is the copy identifier (e.g., barcode), and the value is the due date.
     *
     * @return an unmodifiable map of borrowed copies.
     */
    public Map<String, LocalDate> getBorrowedCopies() {
        return Collections.unmodifiableMap(borrowedCopies);
    }

    /**
     * Returns the number of active loans for this member.
     *
     * @return the size of the borrowed copies map.
     */
    public int activeLoanCount() {
        return borrowedCopies.size();
    }

    /**
     * Check whether the member is under a given loan limit.
     *
     * @param max maximum allowed active loans (precondition: must be &gt;= 0).
     * @return {@code true} if current count is less than {@code max}, {@code false} otherwise.
     */
    public boolean underLoanLimit(int max) {
        return activeLoanCount() < max;
    }

    /**
     * Sets this member's borrowing status to allowed.
     */
    public void allowBorrowing() {
        this.canBorrow = true;
    }

    /**
     * Sets this member's borrowing status to blocked (e.g., due to overdue items).
     */
    public void blockBorrowing() {
        this.canBorrow = false;
    }

    /**
     * Registers that this member has borrowed a copy.
     * The map of borrowed copies is updated, and invariants are checked
     *
     * @param copyKey logical identifier (e.g., barcode). Must not be {@code null}.
     * @param due     due date for this copy. Must not be {@code null}.
     */
    public void addBorrowedCopy(String copyKey, LocalDate due) {
        Preconditions.checkNotNull(copyKey, "copyKey must not be null");
        Preconditions.checkNotNull(due, "due date must not be null");
        borrowedCopies.put(copyKey, due);
        checkMember();
    }

    /**
     * Removes a copy from this member's borrowed list after it is returned.
     *
     * @param copyKey identifier for the copy being returned. Must not be {@code null}.
     */
    public void removeBorrowedCopy(String copyKey) {
        Preconditions.checkNotNull(copyKey, "copyKey must not be null");
        borrowedCopies.remove(copyKey);
        checkMember();
    }

    // Builder

    /**
     * Builder class for safely constructing and validating new {@link Member} objects.
     */
    public static final class MemberBuilder {
        private int memberId;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private String pin;

        /** Simple email format regex. */
        private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

        private boolean canBorrow = true; // default: can borrow

        /**
         * Sets the unique member ID.
         *
         * @param memberId the ID. Must be greater than 0.
         * @return this builder.
         * @throws InvalidAccountException if the ID is invalid.
         */
        public MemberBuilder memberId(int memberId) throws InvalidAccountException{
            if(memberId <= 0){
                throw new InvalidAccountException();
            }
            this.memberId = memberId;
            return this;
        }

        /**
         * Sets the member's first name.
         *
         * @param firstName the first name. Must be non-null, non-blank, and contain only letters.
         * @return this builder.
         * @throws InvalidAccountException if the name is invalid.
         */
        public MemberBuilder firstName(String firstName) throws InvalidAccountException {
            Preconditions.checkNotNull(firstName, "First Name cannot be null");
            if (firstName.isBlank() || !firstName.matches("[A-Za-z]+")) {
                throw new InvalidAccountException();
            }
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the member's last name.
         *
         * @param lastName the last name. Must be non-null, non-blank, and contain only letters.
         * @return this builder.
         * @throws InvalidAccountException if the name is invalid.
         */
        public MemberBuilder lastName(String lastName) throws InvalidAccountException {
            Preconditions.checkNotNull(lastName, "Last Name cannot be null");
            if (lastName.isBlank() || !lastName.matches("[A-Za-z]+")) {
                throw new InvalidAccountException();
            }
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the member's phone number.
         *
         * @param phoneNumber the phone number. Must be exactly 11 digits.
         * @return this builder.
         * @throws InvalidAccountException if the phone number is invalid.
         */
        public MemberBuilder phoneNumber(String phoneNumber) throws InvalidAccountException {
            if (phoneNumber == null || !phoneNumber.matches("\\d{11}")) {
                throw new InvalidAccountException();
            }
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * Sets the member's email address.
         *
         * @param email the email address. Must be non-null, non-blank, and match the email regex.
         * @return this builder.
         * @throws InvalidAccountException if the email is invalid.
         */
        public MemberBuilder email(String email) throws InvalidAccountException {
            if (email == null || email.isBlank() || !email.matches(EMAIL_REGEX)) {
                throw new InvalidAccountException();
            }
            this.email = email;
            return this;
        }

        /**
         * Sets the member's PIN.
         *
         * @param pin the PIN string. Must be exactly 4 digits.
         * @return this builder.
         * @throws InvalidAccountException if the PIN is invalid.
         */
        public MemberBuilder pin(String pin) throws InvalidAccountException {
            if (pin == null || pin.isBlank() || !pin.matches("\\d{4}")) {
                throw new InvalidAccountException();
            }
            this.pin = pin;
            return this;
        }

        /**
         * Sets the member's initial status to allowed to borrow.
         *
         * @return this builder.
         */
        public MemberBuilder canBorrow() {
            this.canBorrow =true;
            return this;
        }

        /**
         * Builds and returns a new {@link Member} object.
         * The member's internal invariants are checked upon construction.
         *
         * @return a new Member instance.
         */
        public Member build() {
            return new Member(memberId,firstName, lastName, phoneNumber, email, pin, canBorrow);
        }
    }
}