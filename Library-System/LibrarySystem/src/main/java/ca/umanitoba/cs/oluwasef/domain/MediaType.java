package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.exceptions.InvalidMediaTypeException;
import com.google.common.base.Preconditions;

/**
 * Represents the physical or digital format of a {@link Media}.
 */
public enum MediaType {
    BOOK,
    DVD,
    BLURAY,
    GAME,
    EBOOK;

    public static MediaType fromString(String value) throws InvalidMediaTypeException {
        Preconditions.checkNotNull(value, "Value passed to enum should not be null");

        return switch(value){
            case "BOOK" -> BOOK;
            case "DVD" ->  DVD;
            case "BLURAY" -> BLURAY;
            case "GAME" -> GAME;
            case "EBOOK" -> EBOOK;
            default -> throw new InvalidMediaTypeException();
        };
    }
}
