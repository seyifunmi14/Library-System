package ca.umanitoba.cs.oluwasef.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private final Copy copy;
    private final Member member;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate;

    public Loan(Copy copy, Member member, LocalDate checkoutDate, LocalDate dueDate) {
        this.copy = Objects.requireNonNull(copy);
        this.member = Objects.requireNonNull(member);
        this.checkoutDate = Objects.requireNonNull(checkoutDate);
        this.dueDate = Objects.requireNonNull(dueDate);
    }

    public Copy getCopy() { return copy; }
    public Member getMember() { return member; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }

    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    public boolean isOverdue() {
        return returnedDate == null && LocalDate.now().isAfter(dueDate);
    }
}