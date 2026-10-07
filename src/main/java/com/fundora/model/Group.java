package com.fundora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Module I & III: Group Domain Entity (FLATMATE_RECURRING or TRIP_EVENT)
 */
@Entity
@Table(name = "fundora_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false, length = 30)
    private GroupType groupType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Group() {
        this.groupType = GroupType.FLATMATE_RECURRING;
        this.createdAt = LocalDateTime.now();
    }

    public Group(String name, String description, GroupType groupType, User createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.groupType = groupType;
        this.createdBy = createdBy;
        if (createdBy != null) {
            this.members.add(createdBy);
        }
    }

    public void addMember(User user) {
        if (user != null) {
            this.members.add(user);
        }
    }

    public void removeMember(User user) {
        if (user != null) {
            this.members.remove(user);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
