package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.MediaCategory;

import java.util.Objects;
import java.util.UUID;

public class DVD extends Media {
    private String regionCode;

    public DVD(UUID id, String title, String creator, MediaCategory category, String regionCode) {
        super(id,title,creator,category);
        this.regionCode = Objects.requireNonNull(regionCode);
    }

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = Objects.requireNonNull(regionCode); }
}
