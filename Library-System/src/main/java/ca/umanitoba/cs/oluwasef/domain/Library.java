package ca.umanitoba.cs.oluwasef.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class  Library {
    private final UUID id;
    private String name;
    private String address;
    private final Set<Media> inventory = new HashSet<>();
    private final Set<Resource> resources = new HashSet<>();
    private FloorMap map;

    public Library(UUID id, String name, String address, FloorMap map) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.address = Objects.requireNonNull(address);
        this.map = Objects.requireNonNull(map);
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getAddress(){return address;}
    public Set<Media> getInventory() {return inventory;}
    public Set<Resource> getResources() {return resources;}
    public FloorMap getMap() {return map;}

    public void setName(String name) {this.name = Objects.requireNonNull(name);}
    public void setAddress(String address) {this.address = Objects.requireNonNull(address);}
    public void setMap(FloorMap map) {this.map = Objects.requireNonNull(map);}

    public void addMedia(Media m){inventory.add(Objects.requireNonNull(m));}
    public void addResource(Resource r){resources.add(Objects.requireNonNull(r));}
}
