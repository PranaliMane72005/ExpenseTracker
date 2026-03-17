package com.financetracker.service;

import com.financetracker.dto.ForumDto;
import com.financetracker.model.ForumComment;
import com.financetracker.model.ForumPost;
import com.financetracker.model.User;
import com.financetracker.repository.ForumCommentRepository;
import com.financetracker.repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumService {

    @Autowired private ForumPostRepository postRepository;
    @Autowired private ForumCommentRepository commentRepository;

    @Transactional
    public ForumPost createPost(User user, ForumDto.PostRequest req) {
        ForumPost post = ForumPost.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public ForumComment addComment(User user, Long postId, ForumDto.CommentRequest req) {
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        ForumComment comment = ForumComment.builder()
                .post(post)
                .user(user)
                .comment(req.getComment())
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public ForumPost likePost(Long postId) {
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        return postRepository.save(post);
    }

    @Transactional
    public ForumComment likeComment(Long commentId) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setLikes(comment.getLikes() + 1);
        return commentRepository.save(comment);
    }

    public List<ForumPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ForumComment> getComments(Long postId) {
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }
}
