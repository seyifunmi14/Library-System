package ca.umanitoba.cs.oluwasef.domain;

import java.time.Instant;
import java.util.Objects;

public class Hold {
    private final Media media;
    private final Member member;
    private final Instant placedAt;

    public Hold(Media media, Member member, Instant placedAt) {
        this.media = Objects.requireNonNull(media);
        this.member = Objects.requireNonNull(member);
        this.placedAt = Objects.requireNonNull(placedAt);
    }

    public Media getMedia() { return media; }
    public Member getMember() { return member; }
    public Instant getPlacedAt() { return placedAt; }
}