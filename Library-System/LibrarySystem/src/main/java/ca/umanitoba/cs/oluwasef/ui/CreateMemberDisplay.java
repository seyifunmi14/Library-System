package ca.umanitoba.cs.oluwasef.ui;

import ca.umanitoba.cs.oluwasef.domain.Member;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidAccountException;
import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import com.google.common.base.Preconditions;

import java.util.Scanner;

/**
 * This class collects raw member information from the user
 * and delegates all validation / creation to MemberManager.
 * This class does ONLY input-format checking (non-empty, basic patterns)
 * and lets the domain layer enforce business rules and invariants.
 */
public final class CreateMemberDisplay {

    private final Scanner keyboard;

    // simple email format
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    /**
     * Creates a new display object for member creation.
     *
     * @param keyboard the Scanner object used to read user input. Must not be {@code null}.
     */
    public CreateMemberDisplay(Scanner keyboard) {
        this.keyboard = Preconditions.checkNotNull(keyboard, "keyboard must not be null");
    }

    /**
     * Prompts the user for member details and calls MemberManager to register.
     *
     * @param memberManager the logic layer manager responsible for handling member creation. Must not be {@code null}.
     * @return the newly created and registered Member object on success, or {@code null} if creation failed.
     */
    public Member run(MemberManager memberManager) {
        Preconditions.checkNotNull(memberManager, "memberManager must not be null");

        System.out.println("Let's create a new member account.");

        // These loops, until the input "looks" valid
        String firstName = promptName("First name");
        String lastName  = promptName("Last name");
        String phone     = promptPhone();
        String email     = promptEmail();
        String pin       = promptPin();

        try {
            Member registered = memberManager.signUp(firstName, lastName, phone, email, pin);

            System.out.println();
            System.out.println("Account created successfully!");
            System.out.println("Your member ID is: " + registered.getMemberId());
            System.out.println("Use this ID + your PIN to log in.");
            return registered;

        } catch (InvalidAccountException e) {
            System.out.println(
                    "We couldn't create your account. " +
                            "Please make sure your information is valid and not already registered."
            );
            return null;
        }
    }

    /**
     * Prompts the user for a name (first or last) and validates it is non-empty and contains only letters.
     *
     * @param label the descriptive label to display to the user (e.g., "First name").
     * @return a valid, non-empty string containing only letters.
     */
    private String promptName(String label) {
        String value = null;
        while (value == null) {
            System.out.print(label + ": ");
            String rawInput = keyboard.nextLine().trim(); // Renamed 'raw' to 'rawInput'

            if (rawInput.isEmpty()) {
                System.out.println(label + " must not be empty.");
                continue;
            }
            if (!rawInput.matches("[A-Za-z]+")) {
                System.out.println(label + " must contain only letters (A–Z).");
                continue;
            }
            value = rawInput;
        }
        return value;
    }

    /**
     * Prompts the user for a phone number and validates it is exactly 11 digits.
     *
     * @return a valid 11-digit phone number string.
     */
    private String promptPhone() {
        String phone = null;
        while (phone == null) {
            System.out.print("Please enter Phone number (11 digits only): ");
            String rawInput = keyboard.nextLine().trim(); // Renamed 'raw' to 'rawInput'

            if (!rawInput.matches("\\d{11}")) {
                System.out.println("Invalid Phone number! Phone number must be 11 digits, with no spaces or dashes.");
                continue;
            }
            phone = rawInput;
        }
        return phone;
    }

    /**
     * Prompts the user for an email address and validates it is non-empty and matches a basic email format.
     *
     * @return a valid email address string.
     */
    private String promptEmail() {
        String email = null;
        while (email == null) {
            System.out.print("Email: ");
            String rawInput = keyboard.nextLine().trim(); // Renamed 'raw' to 'rawInput'

            if (rawInput.isEmpty()) {
                System.out.println("Email must not be empty.");
                continue;
            }
            if (!rawInput.matches(EMAIL_REGEX)) {
                System.out.println("Email must be in the form 'abc@example.com'.");
                continue;
            }
            email = rawInput;
        }
        return email;
    }

    /**
     * Prompts the user to choose a PIN and validates it is exactly 4 digits.
     *
     * @return a valid 4-digit PIN string.
     */
    private String promptPin() {
        String pin = null;
        while (pin == null) {
            System.out.print("Choose a 4-digit PIN: ");
            String rawInput = keyboard.nextLine().trim(); // Renamed 'raw' to 'rawInput'

            if (!rawInput.matches("\\d{4}")) {
                System.out.println("PIN must be exactly 4 digits.");
                continue;
            }
            pin = rawInput;
        }
        return pin;
    }
}