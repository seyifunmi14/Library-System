package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.CopyNotAvailableException;
import ca.umanitoba.cs.oluwasef.exceptions.CopyNotBorrowedException;
import com.google.common.base.Preconditions;

import java.time.LocalDate;

/**
 * Physical copy of a piece of media inside the library system.
 */
public final class Copy {

    /** The unique identifier (e.g., barcode) for this physical copy. */
    private final String barcode;

    /** The current status of the copy (e.g., AVAILABLE, BORROWED). */
    CopyStatus status;

    /** The ID of the {@link Member} who has currently borrowed this copy, or {@code null} if available. */
    private Integer borrowerId;

    /** The date the copy is due to be returned, or {@code null} if available. */
    private LocalDate dueDate;

    /**
     * Constructs a copy using a unique barcode.
     *
     * @param barcode the unique barcode for this copy. Must not be null.
     */
    public Copy(String barcode) {
        this.barcode = barcode;
        this.status = CopyStatus.AVAILABLE;
        this.borrowerId = null;
        this.dueDate = null;

        checkCopy();
    }

    /**
     * Validates all internal invariants of the Copy object.
     * Ensures that fields are consistent with the current {@code status}.
     *
     * @throws IllegalStateException If an invariant is violated (e.g., an AVAILABLE copy has a borrower ID).
     */
    private void checkCopy() {
        Preconditions.checkNotNull(barcode, "barcode must not be null");
        Preconditions.checkState(status != null, "copy status must not be null");

        if (status == CopyStatus.AVAILABLE) {
            Preconditions.checkState(borrowerId == null, "Available copy cannot have borrower");
            Preconditions.checkState(dueDate == null, "Available copy cannot have due date");
        } else {
            Preconditions.checkState(borrowerId != null, "Borrowed copy must have borrower");
            Preconditions.checkState(borrowerId > 0, "Borrower ID must be greater than 0");
            Preconditions.checkState(dueDate != null, "due date cannot be null");
        }
    }


    public String getBarcode() { return barcode; }
    public CopyStatus getStatus() { return this.status; }
    public Integer getBorrowerId() { return borrowerId; }
    public LocalDate getDueDate() { return dueDate; }

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    /**
     * Checks if this copy is currently on loan and past its due date.
     *
     * @param today the current date, used for comparison. Must not be null.
     * @return {@code true} if the copy is borrowed and its due date is before {@code today}; {@code false} otherwise.
     */
    public boolean isOverdue(LocalDate today) {
        Preconditions.checkNotNull(today);
        return status == CopyStatus.BORROWED
                && dueDate != null
                && dueDate.isBefore(today);
    }

    /**
     * Borrows the copy, changing its status to BORROWED and setting the borrower and due date.
     *
     * @param memberId the ID of the member borrowing the copy. Must be &gt; 0.
     * @param dueDate the date the copy is due to be returned. Must not be null.
     * @throws CopyNotAvailableException if the copy is not currently {@code AVAILABLE}.
     */
    public void borrowCopy(int memberId, LocalDate dueDate) throws CopyNotAvailableException {
        if (status != CopyStatus.AVAILABLE) {
            throw new CopyNotAvailableException();
        }

        Preconditions.checkArgument(memberId > 0, "memberId must be greater than 0");
        Preconditions.checkNotNull(dueDate, "dueDate must not be null");

        this.status = CopyStatus.BORROWED;
        this.borrowerId = memberId;
        this.dueDate = dueDate;

        checkCopy();
    }

    /**
     * Returns the copy, changing its status to AVAILABLE and clearing the borrower and due date fields.
     *
     * @throws CopyNotBorrowedException if the copy is not currently {@code BORROWED}.
     */
    public void returnCopy() throws CopyNotBorrowedException {
        if (status != CopyStatus.BORROWED) {
            throw new CopyNotBorrowedException();
        }

        this.status = CopyStatus.AVAILABLE;
        this.borrowerId = null;
        this.dueDate = null;

        checkCopy();
    }
}