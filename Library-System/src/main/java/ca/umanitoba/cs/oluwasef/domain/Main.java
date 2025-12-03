package ca.umanitoba.cs.oluwasef.domain;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

    /**
     * Simple REPL (Create/Read/Update/Delete) demo for the Library System.
     * Demonstrates creating members/media, listing them, creating loans,
     * returning items, and showing system state interactively.
     */
    public class Main {
        private static final Scanner in = new Scanner(System.in);

        public static void main(String[] args) {
            LibrarySystem system = bootstrapDemoData();

            System.out.println("=== Library System REPL ===");
            System.out.println("Type 'help' for available commands.");
            String line;

            while (true) {
                System.out.print("\n> ");
                line = in.nextLine().trim().toLowerCase();

                switch (line) {
                    case "help" -> printHelp();
                    case "list members" -> listMembers(system);
                    case "list media" -> listMedia(system);
                    case "loan" -> createLoan(system);
                    case "return" -> returnLoan(system);
                    case "add member" -> addMember(system);
                    case "quit", "exit" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Unknown command. Type 'help'.");
                }
            }
        }

        private static void printHelp() {
            System.out.println("""
                Commands:
                help           - Show this help text
                  list members   - Show all members
                  list media     - Show all media
                  add member     - Create a new member
                  loan           - Loan a copy of a book
                  return         - Return a loaned copy
                  quit / exit    - End session
                """);
        }

        private static void listMembers(LibrarySystem system) {
            system.getMembers().forEach((id, m) ->
                    System.out.printf("[%s] %s (%s)%n", id, m.getName(), m.getStatus()));
        }

        private static void listMedia(LibrarySystem system) {
            system.getLibraries().values().forEach(l -> {
                System.out.println("Library: " + l.getName());
                for (Media m : l.getInventory()) {
                    System.out.printf(" - %s by %s [%s]%n", m.getTitle(), m.getCreator(), m.getCategory());
                }
            });
        }

        private static void addMember(LibrarySystem system) {
            System.out.print("Enter member name: ");
            String name = in.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }
            Member m = new Member(UUID.randomUUID(), name);
            system.addMember(m);
            System.out.println("Member added: " + name);
        }

        private static void createLoan(LibrarySystem system) {
            Member member = pickMember(system);
            if (member == null) return;

            Library library = system.getLibraries().values().stream().findFirst().orElse(null);
            if (library == null || library.getInventory().isEmpty()) {
                System.out.println("No media available.");
                return;
            }
            Media media = library.getInventory().iterator().next();
            Copy copy = media.getCopies().stream()
                    .filter(c -> c.getStatus() == CopyStatus.AVAILABLE)
                    .findFirst().orElse(null);
            if (copy == null) {
                System.out.println("No available copies.");
                return;
            }
            Loan loan = new Loan(copy, member, LocalDate.now(), LocalDate.now().plusDays(14));
            member.getLoans().add(loan);
            copy.setStatus(CopyStatus.ON_LOAN);
            System.out.printf("%s checked out '%s'.%n", member.getName(), media.getTitle());
        }

        private static void returnLoan(LibrarySystem system) {
            Member member = pickMember(system);
            if (member == null) return;

            if (member.getLoans().isEmpty()) {
                System.out.println("This member has no active loans.");
                return;
            }
            Loan loan = member.getLoans().iterator().next();
            loan.setReturnedDate(LocalDate.now());
            loan.getCopy().setStatus(CopyStatus.AVAILABLE);
            System.out.printf("%s returned '%s'.%n", member.getName(), loan.getCopy().getBarcode());
        }

        private static Member pickMember(LibrarySystem system) {
            if (system.getMembers().isEmpty()) {
                System.out.println("No members in the system.");
                return null;
            }
            System.out.println("Select member ID:");
            listMembers(system);
            System.out.print("ID: ");
            try {
                UUID id = UUID.fromString(in.nextLine().trim());
                Member m = system.findMember(id);
                if (m == null) System.out.println("Invalid member ID.");
                return m;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID format.");
                return null;
            }
        }

        /**
         * Bootstraps a small dataset like your original demo output.
         */
        private static LibrarySystem bootstrapDemoData() {
            LibrarySystem system = new LibrarySystem("City Libraries");
            FloorMap map = new FloorMap(3, 3);
            Library lib = new Library(UUID.randomUUID(), "Downtown Branch", "123 Main St", map);
            system.addLibrary(lib);

            Member alice = new Member(UUID.randomUUID(), "Alice Reader");
            Member bob = new Member(UUID.randomUUID(), "Bob Borrower");
            system.addMember(alice);
            system.addMember(bob);

            Book dune = new Book(UUID.randomUUID(), "Dune", "Frank Herbert", MediaCategory.FANTASY, "9780441172719");
            dune.addCopy(new Copy("BC-1001", "Aisle F1"));
            dune.addCopy(new Copy("BC-1002", "Aisle F1"));
            lib.addMedia(dune);

            Book fiftyShades = new Book(UUID.randomUUID(), "FIFTY SHADES OF GREY", "ANU AKINOLA", MediaCategory.FANTASY, "9790440172719");
            fiftyShades.addCopy(new Copy("BC-1002", "Aisle F2"));
            fiftyShades.addCopy(new Copy("BC-1003", "Aisle F2"));
            lib.addMedia(fiftyShades);

            return system;
        }
    }
