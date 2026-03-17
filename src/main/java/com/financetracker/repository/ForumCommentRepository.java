package com.financetracker.repository;

import com.financetracker.model.ForumComment;
import com.financetracker.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostOrderByCreatedAtAsc(ForumPost post);
}
