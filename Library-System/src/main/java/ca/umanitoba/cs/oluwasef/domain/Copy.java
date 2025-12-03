package ca.umanitoba.cs.oluwasef.domain;

import ca.umanitoba.cs.oluwasef.CopyStatus;

import java.util.Objects;

public class Copy {
    private String barcode;
    private String location;
    private CopyStatus status = CopyStatus.AVAILABLE;

    public Copy(String barcode, String location) {
        this.barcode = Objects.requireNonNull(barcode);
        this.location = Objects.requireNonNull(location);
    }

    public String getBarcode() { return barcode; }
    public String getLocation() { return location; }
    public CopyStatus getStatus() { return status; }

    public void setBarcode(String barcode) { this.barcode = Objects.requireNonNull(barcode); }
    public void setLocation(String location) { this.location = Objects.requireNonNull(location); }
    public void setStatus(CopyStatus status) { this.status = Objects.requireNonNull(status); }
}
