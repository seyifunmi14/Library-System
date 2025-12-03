package ca.umanitoba.cs.oluwasef.Instantiation;

import ca.umanitoba.cs.oluwasef.domain.*;
import ca.umanitoba.cs.oluwasef.exceptions.*;


import java.util.*;

public final class LibraryBuilder {

    private LibraryBuilder() { }

    /**
     * Builds a complete, ready-to-use LibrarySystem:
     * - 1 Library with map
     * - Hard-coded Media (with coordinates)
     * - Hard-coded Resources (with coordinates)
     * - Hard-coded Members
     */
    public static LibrarySystem buildLibraryApp() throws InvalidAccountException, InvalidMediaException, EntityNotFoundException, BookingConflictException, DuplicateEntityException, InvalidMediaCategoryException {
        LibrarySystem system = new LibrarySystem("JOLA LIBRARY SYSTEM");


        // 2) Create branch
        FloorMap map = buildMap();
        Library main = new Library.LibraryBuilder().id(UUID.randomUUID()).name("Jola City Main Library").address("22 Jola Lane").map(map).build();

        // 3) Add media
        buildMedia(main);

        // 4) Add resources
        buildResources(main);

        // 5) Add library into system
        system.addLibrary(main);

        // 6) Add members
        buildMembers(system);

        return system;
    }
    private static FloorMap buildMap() {
        // Legend for map symbols
        Map<Character, String> legend = new LinkedHashMap<>();
        legend.put('#', "Wall");
        legend.put(' ', "Walkway");
        legend.put('E', "Entrance/Exit");


        // Use the builder to make an empty map of the desired size
        FloorMap map = new FloorMap.FloorMapBuilder().width(10).height(6).legend(legend).build();
        //  Fill in the grid contents
        map.populateGrid();
        return map;
    }

    private static void buildMembers(LibrarySystem system) throws InvalidAccountException {
        Member b1 = new Member.MemberBuilder().memberId(1).firstName("Seyi").lastName("Fashola").phoneNumber("16139722220").email("seyifash@gmail.com").pin("2004").canBorrow().build();
        system.addMember(b1);
    }

    private static void buildMedia(Library lib) throws InvalidMediaException {

        Media m1 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Jon Bentley").kind(MediaType.BOOK).title("Programming Pearls").location(new Coordinate(1, 2)).category(MediaCategory.CHILDREN).build();
        Media m2 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Ileri Ayo").kind(MediaType.EBOOK).title("Machine is Coming").location(new Coordinate(2, 2)).category(MediaCategory.NON_FICTION).build();
        Media m3 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("David Promise").kind(MediaType.DVD).title("SnowFall").location(new Coordinate(1, 3)).category(MediaCategory.FICTION).build();
        Media m4 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Seyi Ade").kind(MediaType.BLURAY).title("Heaven in Paradise").location(new Coordinate(1, 4)).category(MediaCategory.FANTASY).build();
        Media m5 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Maggie Slesssor").kind(MediaType.GAME).title("FIFA 2026").location(new Coordinate(2, 4)).category(MediaCategory.SCIENCE).build();
        Media m6 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Fejiro Ama").kind(MediaType.DVD).title("ALEX LUTHER").location(new Coordinate(2, 3)).category(MediaCategory.BIOGRAPHY).build();
        Media m7 = new Media.MediaBuilder().id(UUID.randomUUID()).creator("Temi Abiodun").kind(MediaType.EBOOK).title("THIS IS YOUR FIGHT").location(new Coordinate(2, 1)).category(MediaCategory.BIOGRAPHY).build();


        lib.addMedia(m1);
        lib.addMedia(m2);
        lib.addMedia(m3);
        lib.addMedia(m4);
        lib.addMedia(m5);
        lib.addMedia(m6);
        lib.addMedia(m7);
    }

    private static void buildResources(Library lib) {

        Resource r1 = new Resource.ResourceBuilder().resourceId(1).location(new Coordinate(3, 2)).description("Study Room R1").kind(ResourceType.ROOM).resourceName("Study room").build();
        Resource r2 = new Resource.ResourceBuilder().resourceId(2).location(new Coordinate(4, 3)).description("Computer 1").kind(ResourceType.COMPUTER).resourceName("Computer").build();
        Resource r3 = new Resource.ResourceBuilder().resourceId(3).location(new Coordinate(1, 2)).description("Study Desk 4").kind(ResourceType.STUDY_DESK).resourceName("Study Desk").build();
        Resource r4 = new Resource.ResourceBuilder().resourceId(4).location(new Coordinate(2, 2)).description("PRINTER 3").kind(ResourceType.PRINTER).resourceName("Printer").build();

        lib.addResource(r1);
        lib.addResource(r2);
        lib.addResource(r3);
        lib.addResource(r4);
    }
}