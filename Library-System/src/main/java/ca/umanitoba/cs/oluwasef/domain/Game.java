package ca.umanitoba.cs.oluwasef.domain;

import java.util.Objects;
import java.util.UUID;

public class Game extends Media {
    private String platform;

    public Game(UUID id, String title, String creator, MediaCategory category, String platform) {
        super(id,title,creator,category);
        this.platform = Objects.requireNonNull(platform);
    }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = Objects.requireNonNull(platform); }
}
