package ca.umanitoba.cs.oluwasef.Instantiation;

import ca.umanitoba.cs.oluwasef.domain.LibrarySystem;
import ca.umanitoba.cs.oluwasef.exceptions.*;
import ca.umanitoba.cs.oluwasef.logic.BookingManager;
import ca.umanitoba.cs.oluwasef.logic.MapManager;
import ca.umanitoba.cs.oluwasef.logic.MediaManager;
import ca.umanitoba.cs.oluwasef.logic.MemberManager;
import ca.umanitoba.cs.oluwasef.ui.LibraryHomeDisplay;
import ca.umanitoba.cs.oluwasef.ui.SignInDisplay;

import java.util.Scanner;

/**
 * Program entry point for the Library System app.
 * Wires together:
 *   - Domain layer (LibrarySystem, Media, Member, Resource, etc.)
 *   - Logic layer (Managers)
 *   - UI layer (Displays)
 * Also acts as a global safety net for top-level exceptions.
 */
public final class LibraryAppMain {

    private LibraryAppMain() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        try  {
            LibrarySystem system = LibraryBuilder.buildLibraryApp();
            Scanner keyboard = new Scanner(System.in);
            MemberManager memberManager = new MemberManager(system);
            MediaManager mediaManager = new MediaManager(memberManager);
            BookingManager bookingManager = new BookingManager(memberManager);
            MapManager mapManager = new MapManager(system);

            // Sign-up / Login flow
            System.out.println("Welcome to JOLA City Library System!");

            while (true) {
                SignInDisplay signInDisplay = new SignInDisplay(keyboard, memberManager);
                boolean loggedIn = signInDisplay.run();

                if (!loggedIn) {
                    System.out.println("Goodbye!");
                    break;
                }

                // User is now logged in → show home menu
                LibraryHomeDisplay homeDisplay = new LibraryHomeDisplay(
                        keyboard,
                        memberManager,
                        mediaManager,
                        bookingManager,
                        mapManager
                );
                homeDisplay.run();
            }
        } catch (InvalidAccountException e) {
            System.err.println("Error: The account you attempted to access is not valid.");
        } catch (InvalidMediaException e) {
            System.err.println("Error: Invalid or corrupted media data.");
        } catch (InvalidResourceException e) {
            System.err.println("Error: Invalid or corrupted resource data.");
        } catch (EntityNotFoundException e) {
            System.err.println("Error: The requested item could not be found.");
        } catch (BookingConflictException e) {
            System.err.println("Error: That resource booking time is unavailable.");
        } catch (DuplicateEntityException e) {
            System.err.println("Error: That ID already exists in the system.");
        } catch (InvalidMediaCategoryException e) {
            System.err.println("Unexpected error. Please restart the system.");
        }
    }
}