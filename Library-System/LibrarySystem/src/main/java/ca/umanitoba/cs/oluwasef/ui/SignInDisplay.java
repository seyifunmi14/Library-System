package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.domain.Member;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidAccountException;
import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import com.google.common.base.Preconditions;

import java.util.Scanner;

/**
 * This class handles the sign-up and log-in user interface for the library system.
 * It prompts the user to either create a new member account or log into an existing one,
 * and delegates all validation and account management to {@link MemberManager} and
 * {@link CreateMemberDisplay}.
 */
public final class SignInDisplay {

    private final Scanner keyboard;
    private final MemberManager memberManager;

    /**
     * Constructs a new SignInDisplay to manage the sign-up / log-in flow.
     * @param keyboard      the scanner used to read input from the user
     * @param memberManager the logic component that manages members and logins
     */
    public SignInDisplay(Scanner keyboard, MemberManager memberManager) {
        this.keyboard = Preconditions.checkNotNull(keyboard, "keyboard must not be null");
        this.memberManager = Preconditions.checkNotNull(memberManager, "memberManager must not be null");
    }

    /**
     * This method displays the main sign-in menu (Sign up, Log in, Quit) and
     * loops until the user either successfully signs in (new or existing) or chooses to quit.
     * @return {@code true} if sign-up or login succeeds and the user is authenticated,<br>
     *         {@code false} if the user chooses to quit at the sign-in menu
     */
    public boolean run() {
        String choice;

        do {
            System.out.println();
            System.out.println("=== Jola City Library ===");
            System.out.println("1) Sign up");
            System.out.println("2) Log in");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");

            choice = keyboard.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    // If sign-up returns a valid member, treat that as a successful sign-in
                    if (handleSignUp()) {
                        return true;
                    }
                }
                case "2" -> {
                    // If login succeeds, user is authenticated
                    if (handleLogin()) {
                        return true;
                    }
                }
                case "3" -> {
                    // User chose to quit the application from the sign-in menu
                    return false;
                }
                default -> System.out.println("Unknown option: " + choice);
            }
        } while (true);
    }

    /**
     * This method handles creating a new member account.
     * It delegates the input prompts and validation to {@link CreateMemberDisplay},
     * and returns whether a member was successfully created.
     * @return {@code true} if a new member was successfully created,<br>
     *         {@code false} otherwise (for example, if creation was cancelled or failed)
     */
    private boolean handleSignUp() {
        System.out.println();
        System.out.println("--- Sign up ---");

        CreateMemberDisplay creator = new CreateMemberDisplay(keyboard);
        Member member = creator.run(memberManager);

        // If creation failed or was cancelled, member will be null
        return member != null;
    }

    /**
     * This method handles logging in an existing member using member ID and PIN.
     * It prompts for credentials, calls {@link MemberManager#login(int, String)},
     * and prints user-friendly error messages if anything goes wrong.
     * @return {@code true} if the login succeeds and a member is now logged in,<br>
     *         {@code false} otherwise
     */
    private boolean handleLogin() {
        System.out.println();
        System.out.println("--- Log in ---");

        // Prompt for ID
        System.out.print("Member ID: ");
        String idText = keyboard.nextLine().trim();

        // Prompt for PIN
        System.out.print("PIN: ");
        String pin = keyboard.nextLine().trim();

        try {
            int memberId = Integer.parseInt(idText);

            memberManager.login(memberId, pin);

            // If login succeeds, MemberManager tracks the current user
            System.out.println("Logged in as " + memberManager.getCurrentUser().getFullName());
            return true;

        } catch (NumberFormatException e) {
            // ID was not a valid integer
            System.out.println("Member ID must be a whole number.");
        } catch (InvalidAccountException e) {
            // Combination of ID + PIN was not valid
            System.out.println("Invalid ID/PIN combination. Please try again.");
        } catch (EntityNotFoundException e) {
            // No member exists with that ID
            System.out.println("Member not found");
        }
        // Login did not succeed
        return false;
    }
}