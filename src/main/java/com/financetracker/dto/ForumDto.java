package com.financetracker.dto;

import com.financetracker.model.ForumComment;
import com.financetracker.model.ForumPost;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ForumDto {

    public static class PostRequest {
        @NotBlank private String title;
        @NotBlank private String content;
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public void setTitle(String v) { this.title = v; }
        public void setContent(String v) { this.content = v; }
    }

    public static class PostResponse {
        private Long id;
        private String authorName;
        private String title;
        private String content;
        private Integer likes;
        private int commentCount;
        private LocalDateTime createdAt;

        public static PostResponse from(ForumPost p) {
            PostResponse r = new PostResponse();
            r.id = p.getId(); r.authorName = p.getUser().getName();
            r.title = p.getTitle(); r.content = p.getContent();
            r.likes = p.getLikes(); r.commentCount = p.getComments().size();
            r.createdAt = p.getCreatedAt();
            return r;
        }
        public Long getId() { return id; }
        public String getAuthorName() { return authorName; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public Integer getLikes() { return likes; }
        public int getCommentCount() { return commentCount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class CommentRequest {
        @NotBlank private String comment;
        public String getComment() { return comment; }
        public void setComment(String v) { this.comment = v; }
    }

    public static class CommentResponse {
        private Long id;
        private String authorName;
        private String comment;
        private Integer likes;
        private LocalDateTime createdAt;

        public static CommentResponse from(ForumComment c) {
            CommentResponse r = new CommentResponse();
            r.id = c.getId(); r.authorName = c.getUser().getName();
            r.comment = c.getComment(); r.likes = c.getLikes(); r.createdAt = c.getCreatedAt();
            return r;
        }
        public Long getId() { return id; }
        public String getAuthorName() { return authorName; }
        public String getComment() { return comment; }
        public Integer getLikes() { return likes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
