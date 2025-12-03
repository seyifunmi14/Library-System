package ca.umanitoba.cs.oluwasef.domain;

import java.lang.reflect.Member;
import java.util.*;

public abstract class Media implements Reviewable {
    private final UUID id;
    private String title;
    private String creator;
    private MediaCategory category;

    private final Set<Copy> copies = new HashSet<>();
    private final Queue<Member> waitlist = new ArrayDeque<>();
    private final List<Review> reviews = new ArrayList<>();

    public Media(UUID id, String title, String creator, MediaCategory category){
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.creator = Objects.requireNonNull(creator);
        this.category = Objects.requireNonNull(category);
    }

    public UUID getId() {return id;}
    public String getTitle() {return title;}
    public String getCreator() {return creator;}
    public MediaCategory getCategory() {return category;}
    public Set<Copy> getCopies() {return copies;}
    public Queue<Member> getWaitlist() {return waitlist;}

    public void setTitle(String title) {this.title = Objects.requireNonNull(title);}
    public void setCreator(String creator) {this.creator = Objects.requireNonNull(creator);}
    public void setCategory(MediaCategory category) {this.category = Objects.requireNonNull(category);}

    public void addCopy(Copy copy) {copies.add(Objects.requireNonNull(copy));}
    public void enqueueWaitlist(Member m) {waitlist.add(Objects.requireNonNull(m));}
    public Member dequeueWaitlist(){return waitlist.poll();}

    public void addReview(Review r) {reviews.add(Objects.requireNonNull(r));}
    public List<Review> getReviews() {return Collections.unmodifiableList(reviews);}
}