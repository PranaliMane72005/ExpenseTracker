package com.financetracker.controller;

import com.financetracker.dto.ForumDto;
import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired private ForumService forumService;
    @Autowired private AuthService authService;

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts() {
        return ResponseEntity.ok(
                forumService.getAllPosts().stream()
                        .map(ForumDto.PostResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ForumDto.PostRequest req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ForumDto.PostResponse.from(forumService.createPost(user, req)));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        return ResponseEntity.ok(ForumDto.PostResponse.from(forumService.likePost(postId)));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(
                forumService.getComments(postId).stream()
                        .map(ForumDto.CommentResponse::from)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody ForumDto.CommentRequest req) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ForumDto.CommentResponse.from(
                forumService.addComment(user, postId, req)));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(ForumDto.CommentResponse.from(forumService.likeComment(commentId)));
    }
}
