package com.indiaexplorer.repository;

import com.indiaexplorer.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // This allows you to fetch all comments for a specific post easily
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}