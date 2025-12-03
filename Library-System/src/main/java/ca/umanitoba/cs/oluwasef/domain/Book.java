package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.MediaCategory;

import java.util.Objects;
import java.util.UUID;

public class Book extends Media {
    private String isbn;

    public Book (UUID id, String title, String creator, MediaCategory category, String isbn) {
        super(id,title,creator,category);
        this.isbn = Objects.requireNonNull(isbn);

    }

    public String getIsbn() {return isbn;}
    public void setIsbn(String isbn) {this.isbn = Objects.requireNonNull(isbn);}
}