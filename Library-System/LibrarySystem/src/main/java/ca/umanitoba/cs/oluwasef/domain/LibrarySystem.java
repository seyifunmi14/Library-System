package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.*;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.*;

/**
 * The central management system for the library. The system is responsible for
 * managing all registered {@link Library} branches, controlling {@link Member}
 * registration, and handling core lending operations such as borrowing and returning media.
 */
public final class LibrarySystem {

    /** Maximum number of active loans permitted per member at any given time. */
    private static final int MAX_ACTIVE_LOANS = 10;

    /** A map storing all registered library branches, keyed by the library's String ID. */
    private final Map<String, Library> libraries = new HashMap<>();

    /** A map storing all registered members, keyed by their unique integer memberId. */
    private final Map<Integer, Member> members = new HashMap<>();

    /**
     * Counter used to generate the next unique member ID.
     * New member IDs start at 1001, 1002, and so on.
     */
    private int nextMemberId = 1000;

    /**
     * Constructs a library system with the given name.
     *
     * @param name the name of the library system. Must not be {@code null} or empty.
     * @throws NullPointerException If name is null.
     * @throws IllegalArgumentException If name is empty.
     */
    public LibrarySystem(String name) {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkArgument(!name.isEmpty(), "name must be non-empty");
        checkLibrarySystem();
    }

    /**
     * Validates the internal invariants of the library system, ensuring that
     * collections are not null and do not contain null keys or values.
     *
     * @throws IllegalStateException If any system invariant is violated.
     */
    private void checkLibrarySystem() {
        Preconditions.checkNotNull(libraries, "Libraries must not be null");
        Preconditions.checkNotNull(members, "Members must not be null");
        Preconditions.checkArgument(nextMemberId >= 1000, "Next memberId must be >= 1000 (system starts assigning IDs at 1001)");
        for (Map.Entry<String, Library> entry : libraries.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Found null key in libraries map!");
            Preconditions.checkNotNull(entry.getValue(), "Found null Library value in libraries map!");
        }
        for (Map.Entry<Integer, Member> entry : members.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Found null key in members map!");
            Preconditions.checkNotNull(entry.getValue(), "Found null Member value in members map!");
        }
    }

    /**
     * Retrieves an unmodifiable view of all registered library branches.
     * @return An unmodifiable map of Library IDs to {@link Library} objects.
     */
    public Map<String, Library> getLibraries() {
        return Collections.unmodifiableMap(libraries);
    }
    /**
     * Registers a new library branch with the system.
     *
     * @param lib The {@link Library} to add. Must not be null.
     * @throws NullPointerException If {@code lib} is null.
     * @throws IllegalArgumentException If a library with the same ID already exists.
     */
    public void addLibrary(Library lib) {
        Preconditions.checkNotNull(lib, "library must not be null");
        String key = lib.getId().toString();
        Preconditions.checkArgument(!libraries.containsKey(key),
                "A library with key %s already exists", key);
        libraries.put(key, lib);
        checkLibrarySystem();
    }

    /**
     * Adds an already constructed {@link Member} object to the system.
     *
     * @param m The {@code Member} to add. Must not be null.
     * @throws NullPointerException If {@code m} is null.
     * @throws IllegalArgumentException If a member with the same ID already exists.
     * @throws InvalidAccountException If a member with the same name or email (case-insensitive) already exists.
     */
    public void addMember(Member m) throws InvalidAccountException {
        Preconditions.checkNotNull(m, "member must not be null");
        int key = m.getMemberId();

        // no duplicate IDs
        Preconditions.checkArgument(!members.containsKey(key),
                "A member with ID %s already exists", key);

        // prevent duplicate by name or email (case-insensitive)
        for (Member existing : members.values()) {
            if (existing.getFullName().equalsIgnoreCase(m.getFullName())
                    || existing.getEmail().equalsIgnoreCase(m.getEmail())) {
                throw new InvalidAccountException();
            }
        }

        members.put(key, m);
        checkLibrarySystem();
    }

    /**
     * Registers a new member in the system, assigns an auto-generated ID, and returns the newly created member.
     * ID is auto-generated (e.g., 1001, 1002, ...).
     *
     * @param firstName  member's first name. Must not be null or empty.
     * @param lastName member's last name. Must not be null or empty.
     * @param phone phone number. Must be 11 digits.
     * @param email email address. Must be non-blank and contain '@'.
     * @param pin   4-digit PIN. Must be a non-null string containing exactly four digits.
     * @return The newly registered {@link Member} object.
     * @throws InvalidAccountException If any input is invalid (null, empty, or format incorrect),
     * or if a member with the same name/email already exists.
     */
    public Member registerMember(String firstName, String lastName, String phone, String email, String pin)
            throws InvalidAccountException {

        // first name must not be null or blank
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidAccountException();
        }
        // last name must not be null or blank
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidAccountException();
        }
        // phone number must be 11 digits
        if (phone == null || !phone.matches("\\d{11}")) {
            throw new InvalidAccountException();
        }
        // Email must not be blank and must contain '@'
        if (email == null || email.trim().isEmpty() || !email.contains("@")){
            throw new InvalidAccountException();
        }
        // Pin must be exactly 4 digits
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new InvalidAccountException();
        }
        // generate new Id
        int newId = generateMemberId();

        // Build Member using the builder
        Member m = new Member.MemberBuilder()
                .memberId(newId)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phone)
                .email(email)
                .pin(pin)
                .canBorrow()
                .build();

        addMember(m);
        return m;
    }


    /**
     * Finds a registered member by their unique ID.
     *
     * @param memberId The ID of the member to find. Must be positive.
     * @return The {@link Member} object corresponding to the ID, or {@code null} if not found.
     * @throws IllegalArgumentException If {@code memberId} is not greater than 0.
     */
    public Member findMemberById(int memberId) {
        Preconditions.checkArgument(memberId > 0, "memberId must be greater than 0");
        return members.get(memberId);
    }

    /**
     * Checks whether the given member has any overdue media copies across all registered libraries.
     *
     * @param member The {@link Member} to check. Must not be null.
     * @param today The current date, used to determine if a loan is overdue. Must not be null.
     * @return {@code true} if the member has at least one overdue copy; {@code false} otherwise.
     * @throws NullPointerException If {@code member} or {@code today} is null.
     */
    public boolean hasOverdueMedia(Member member, LocalDate today) {
        Preconditions.checkNotNull(member, "member must not be null");
        Preconditions.checkNotNull(today, "today must not be null");
        checkLibrarySystem();

        int id = member.getMemberId();

        for (Library library : libraries.values()) {
            for (Media media : library.getMedia()) {
                for (Copy copy : media.getCopies()) {
                    // if this copy belongs to this member and is overdue
                    if (!copy.isAvailable()
                            && copy.getBorrowerId() != null
                            && copy.getBorrowerId().equals(id)
                            && copy.isOverdue(today)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Borrows a media item for a member from a given library.
     * If an available copy exists, it is loaned out for 14 days. If no copy is available,
     * the member is placed on the media's waitlist.
     *
     * @param library The {@link Library} where the media is located. Must not be null.
     * @param mediaId The ID of the media item. Must not be null.
     * @param member The {@link Member} attempting to borrow. Must not be null.
     * @param today The current date. Must not be null.
     * @throws BorrowingException If the member is blocked, over the loan limit (10 items), or has overdue media.
     * @throws EntityNotFoundException If the media item is not found in the library.
     * @throws NullPointerException If any required parameter is null.
     */
    public void borrowMedia(Library library,
                            String mediaId,
                            Member member,
                            LocalDate today)
            throws BorrowingException, EntityNotFoundException {

        Preconditions.checkNotNull(library, "library must not be null");
        Preconditions.checkNotNull(member, "member must not be null");
        Preconditions.checkNotNull(today, "today must not be null");
        checkLibrarySystem();

        //  member must be allowed to borrow
        if (!member.canBorrow()) {
            throw new BorrowingException();
        }

        // member must be under loan limit (MAX_ACTIVE_LOANS)
        if (!member.underLoanLimit(MAX_ACTIVE_LOANS)) {
            throw new BorrowingException();
        }

        //  member must NOT have overdue media
        if (hasOverdueMedia(member, today)) {
            // Block borrowing immediately upon finding overdue media
            member.blockBorrowing();
            throw new BorrowingException();
        }

        //  find the media
        Media media = library.requireMedia(mediaId);

        // find an available copy, or else join waitlist
        Optional<Copy> available = media.getCopies().stream()
                .filter(c -> c.getStatus() == CopyStatus.AVAILABLE)
                .findFirst();

        if (available.isPresent()) {
            Copy copy = available.get();
            LocalDate due = today.plusDays(14);

            try {
                copy.borrowCopy(member.getMemberId(), due);
            } catch (CopyNotAvailableException e) {
                // Should be unreachable if logic is correct
                throw new BorrowingException();
            }
            // Track logical loan on member
            member.addBorrowedCopy(copy.getBarcode(), due);

        } else {
            // No copies available → member joins the waitlist
            media.joinWaitlist(member);
        }
    }

    /**
     * Processes the return of a media copy previously borrowed by a member.
     * The copy is returned, the member's loan record is updated, and a review is added if provided.
     *
     * @param library The {@link Library} where the media is cataloged. Must not be null.
     * @param mediaId The ID of the media item. Must not be null.
     * @param copyBarcode The unique barcode of the copy being returned. Must not be null.
     * @param member The {@link Member} returning the media (must be the borrower). Must not be null.
     * @param today The current date. Must not be null.
     * @param ratingOrNull The optional rating (1-5) for the review, or {@code null} if no review is left.
     * @param reviewTextOrNull The optional text for the review, or {@code null} if no review is left.
     * @throws BorrowingException If the copy was not on loan to this member, or if the copy state is inconsistent.
     * @throws EntityNotFoundException If the media or copy is not found.
     * @throws InvalidReviewException If the optional review data is provided but invalid (e.g., rating out of range).
     * @throws NullPointerException If any required parameter is null.
     */
    public void returnMedia(Library library,
                            String mediaId,
                            String copyBarcode,
                            Member member,
                            LocalDate today,
                            Integer ratingOrNull,
                            String reviewTextOrNull)
            throws  BorrowingException, EntityNotFoundException, InvalidReviewException {

        Preconditions.checkNotNull(library, "library must not be null");
        Preconditions.checkNotNull(mediaId, "mediaId must not be null when returning media");
        Preconditions.checkNotNull(copyBarcode, "copyBarcode must not be null when returning media"); // Added check
        Preconditions.checkNotNull(member, "member must not be null"); // Added check
        Preconditions.checkNotNull(today, "today must not be null");
        checkLibrarySystem();

        Media media = library.requireMedia(mediaId);
        // find copy
        Copy copy = media.getCopies().stream()
                .filter(c -> c.getBarcode().equals(copyBarcode))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        // Pre-condition: must be returning their own loan
        if (copy.getBorrowerId() == null ||
                !copy.getBorrowerId().equals(member.getMemberId())) {
            throw new BorrowingException();
        }

        try {
            copy.returnCopy();
        } catch (CopyNotBorrowedException e) {
            throw new BorrowingException();
        }

        // update member’s logical loan list
        member.removeBorrowedCopy(copy.getBarcode());

        // Restore borrowing privileges if no longer has overdue media
        if (!hasOverdueMedia(member, today)) {
            member.allowBorrowing();
        }

        // serve next person in waitlist, if any
        Member next = media.pollNextFromWaitlist();
        if (next != null) {
            LocalDate due = today.plusDays(14);
            try {
                copy.borrowCopy(next.getMemberId(), due);
            } catch (CopyNotAvailableException e) {
                throw new BorrowingException();
            }
            // Track logical loan on next member
            next.addBorrowedCopy(copy.getBarcode(), due);
        }

        // Process Review
        if (ratingOrNull != null &&
                reviewTextOrNull != null &&
                !reviewTextOrNull.isBlank()) {
            media.addReview(
                    member.getMemberId(),
                    ratingOrNull,
                    reviewTextOrNull
            );
        }
    }

    /**
     * Increments the internal member ID counter and returns the new unique ID.
     * The ID starts at 1001.
     * @return A new, unique member ID.
     */
    private int generateMemberId() {
        return ++nextMemberId;
    }

    /**
     * Returns the maximum number of active loans permitted for a single member.
     * @return The maximum number of active loans (currently 10).
     */
    public static int getMaxActiveLoans() {
        return MAX_ACTIVE_LOANS;
    }
}