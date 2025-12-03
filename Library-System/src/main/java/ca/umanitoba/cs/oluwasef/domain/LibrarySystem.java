package ca.umanitoba.cs.oluwasef.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Root aggregate that tracks Libraries and Members.
 * Invariants:
 *  - name != null && !name.isBlank()
 *  - library/member IDs are unique within their maps
 */
public class LibrarySystem {

    private final String name;
    private final Map<UUID, Library> libraries = new HashMap<>();
    private final Map<UUID, Member> members = new HashMap<>();

    public LibrarySystem(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getName() { return name; }

    /** Add a library to the system. */
    public void addLibrary(Library l) {
        Objects.requireNonNull(l, "library must not be null");
        libraries.put(l.getId(), l);
    }

    /** Add a member to the system. */
    public void addMember(Member m) {
        Objects.requireNonNull(m, "member must not be null");
        members.put(m.getId(), m);
    }

    public Member findMember(UUID id) { return members.get(id); }

    public Library findLibrary(UUID id) { return libraries.get(id); }

    /* Optional: expose maps for your REPL "list" commands */
    public Map<UUID, Library> getLibraries() { return libraries; }
    public Map<UUID, Member> getMembers() { return members; }
}