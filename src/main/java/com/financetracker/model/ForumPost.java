package com.financetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forum_posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer likes = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumComment> comments = new ArrayList<>();

    public ForumPost() {}

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Integer getLikes() { return likes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ForumComment> getComments() { return comments; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setComments(List<ForumComment> comments) { this.comments = comments; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ForumPost p = new ForumPost();
        public Builder user(User v) { p.user = v; return this; }
        public Builder title(String v) { p.title = v; return this; }
        public Builder content(String v) { p.content = v; return this; }
        public Builder likes(Integer v) { p.likes = v; return this; }
        public ForumPost build() { return p; }
    }
}
