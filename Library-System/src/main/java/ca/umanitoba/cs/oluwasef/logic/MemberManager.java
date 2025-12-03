package ca.umanitoba.cs.oluwasef.logic;

import ca.umanitoba.cs.oluwasef.domain.LibrarySystem;
import ca.umanitoba.cs.oluwasef.domain.Member;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidAccountException;
import com.google.common.base.Preconditions;

/**
 * Logic layer for member sign-up, login, and tracking the current user.
 */
public final class MemberManager {

    private final LibrarySystem system;
    private Member currentUser;

    public MemberManager(LibrarySystem system) {
        this.system = Preconditions.checkNotNull(system);
    }

    public LibrarySystem getSystem() {
        return system;
    }

    public Member getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * New overload: sign up using a fully built Member from the UI.
     */
    public Member signUp(String firstName,
                         String lastName,
                         String phone,
                         String email,
                         String pin) throws InvalidAccountException {
        Preconditions.checkNotNull(firstName, "firstName must not be null");
        Preconditions.checkNotNull(lastName,  "lastName must not be null");
        Preconditions.checkNotNull(phone,     "phone must not be null");
        Preconditions.checkNotNull(email,     "email must not be null");
        Preconditions.checkNotNull(pin,       "pin must not be null");

        Member m = system.registerMember(firstName, lastName, phone, email, pin);
        this.currentUser = m;
        return m;
    }

    public void login(int memberId, String pin)
            throws EntityNotFoundException, InvalidAccountException {

        Preconditions.checkArgument(memberId > 0, "memberId must be > 0");
        Preconditions.checkNotNull(pin, "PIN must not be null");

        Member found = system.findMemberById(memberId);
        if (found == null) {
            throw new EntityNotFoundException();
        }

        if (!found.getPin().equals(pin)) {
            throw new InvalidAccountException();
        }

        currentUser = found;
    }

    public void logout() {
        currentUser = null;
    }
}