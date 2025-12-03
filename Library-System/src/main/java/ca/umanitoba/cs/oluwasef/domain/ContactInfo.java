package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.ContactType;

import java.util.Objects;

public class ContactInfo {
    private ContactType type;
    private String value;
    private boolean preferred;

    public ContactInfo(ContactType type, String value, boolean preferred){
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this.preferred = preferred;
    }

    public ContactType getType() {return type;}
    public String getValue() {return value;}
    public boolean isPreferred() {return preferred;}

    public void setType(ContactType type) {this.type = Objects.requireNonNull(type);}
    public void setValue(String value) {this.value = Objects.requireNonNull(value);}
    public void setPreferred(boolean preferred) {this.preferred = preferred;}
}