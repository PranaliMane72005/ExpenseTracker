package com.financetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forum_comments")
public class ForumComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    private Integer likes = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ForumComment() {}

    // Getters
    public Long getId() { return id; }
    public ForumPost getPost() { return post; }
    public User getUser() { return user; }
    public String getComment() { return comment; }
    public Integer getLikes() { return likes; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPost(ForumPost post) { this.post = post; }
    public void setUser(User user) { this.user = user; }
    public void setComment(String comment) { this.comment = comment; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ForumComment c = new ForumComment();
        public Builder post(ForumPost v) { c.post = v; return this; }
        public Builder user(User v) { c.user = v; return this; }
        public Builder comment(String v) { c.comment = v; return this; }
        public Builder likes(Integer v) { c.likes = v; return this; }
        public ForumComment build() { return c; }
    }
}
