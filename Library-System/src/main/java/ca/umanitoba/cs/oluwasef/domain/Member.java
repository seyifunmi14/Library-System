package ca.umanitoba.cs.oluwasef.domain;

import java.util.*;

public class Member{
    private String name;
    private final Set<ContactInfo> contacts = new HashSet<>();
    private final UUID id;
    private AccountStatus status = AccountStatus.ACTIVE;

    private final List<Review> reviews = new ArrayList<>();
    private final Set<Hold> holds = new HashSet<>();
    private final Set<Loan> loans = new HashSet<>();

    public Member(UUID id, String name){
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public Set<ContactInfo> getContacts() {return contacts;}
    public AccountStatus getStatus() {return status;}
    public List<Review>  getReviews() {return reviews;}
    public Set<Hold> getHolds() {return holds;}
    public Set<Loan> getLoans() {return loans;}

    public void setName(String name) {this.name = Objects.requireNonNull(name);}
    public void setStatus(AccountStatus status) {this.status = Objects.requireNonNull(status);}
}
