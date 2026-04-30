package com.indiaexplorer.repository;

import com.indiaexplorer.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // ✅ Existing: all comments for a post
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // ✅ NEW: get only main comments (no parent)
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId);

    // ✅ NEW: get replies of a comment
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentId);
}