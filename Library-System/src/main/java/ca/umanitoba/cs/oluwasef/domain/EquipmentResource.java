package ca.umanitoba.cs.oluwasef.domain;

import java.util.Objects;
import java.util.UUID;

public class EquipmentResource extends Resource {
    private String type;

    public EquipmentResource(UUID id, String name, String description, String type) {
        super(id,name,description);
        this.type = Objects.requireNonNull(type);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = Objects.requireNonNull(type); }
}
