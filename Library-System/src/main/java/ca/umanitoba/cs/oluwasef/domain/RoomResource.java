package ca.umanitoba.cs.oluwasef.domain;

import java.util.UUID;

public class RoomResource extends Resource {
    private int capacity;

    public RoomResource(UUID id, String name, String description, int capacity) {
        super(id,name,description);
        this.capacity = capacity;
    }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}