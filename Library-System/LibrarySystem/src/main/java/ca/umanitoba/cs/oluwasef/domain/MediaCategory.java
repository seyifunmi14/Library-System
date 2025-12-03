package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidMediaCategoryException;
import com.google.common.base.Preconditions;

/**
 * Enumerates thematic categories of media (e.g., Fiction, Non-Fiction, Fantasy).
 */
public enum MediaCategory {
    FICTION,
    NON_FICTION,
    SCIENCE,
    FANTASY,
    HISTORY,
    BIOGRAPHY,
    CHILDREN;

    public static MediaCategory fromString(String value) throws InvalidMediaCategoryException {
        Preconditions.checkNotNull(value, "Value passed to enum should not be null");

        return switch(value){
            case "FICTION" -> FICTION;
            case "NON_FICTION" ->  NON_FICTION;
            case "SCIENCE" -> SCIENCE;
            case "FANTASY" -> FANTASY;
            case "HISTORY" -> BIOGRAPHY;
            case "CHILDREN" -> CHILDREN;
            default -> throw new InvalidMediaCategoryException();
        };
    }
}
