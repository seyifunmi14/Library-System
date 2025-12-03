package ca.umanitoba.cs.oluwasef.output;

import ca.umanitoba.cs.oluwasef.domain.Library;

/**
 * Prints {@link Library} information in a readable format.
 * Each printer instance is associated with one Library.
 */
public final class LibraryPrinter {

    private final Library library;  // matches your UML

    /**
     * Constructs a LibraryPrinter for the given library.
     * @param library the Library to print; must not be null
     */
    public LibraryPrinter(Library library) {
        this.library = library;
    }

    /**
     * Prints this printer’s Library summary.
     */
    public void print() {
        if (library == null) {
            System.out.println("Library not found");
            return;
        }
        System.out.println("== LIBRARY ==");
        System.out.println("ID: " + library.getId());
        System.out.println("Name: " + library.getName());
        System.out.println("Address: " + library.getAddress());
        System.out.println("#Media: " + library.getMedia().size()
                + "  #Resources: " + library.getResources().size());
    }
}