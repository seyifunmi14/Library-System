package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.Member;

/**
 * A member printer. Formats {@link Member} information for display.
 * Instance-based to match UML (MemberPrinter --o Member).
 */
public final class MemberPrinter {
    private final Member member;

    /**
     * Constructs a MemberPrinter for the specified member.
     * @param member the member to print; can be {@code null}.
     */
    public MemberPrinter(Member member) {
        this.member = member;
    }

    /**
     * Prints this printer's member details to standard output.
     */
    public void print() {
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.println("== MEMBER DETAILS ==");
        System.out.println("Name: " + member.getFullName());
        System.out.println("Phone: " + member.getPhoneNumber());
        System.out.println("Email: " + member.getEmail());
        System.out.println("Pin: " + member.getPin());
    }
}