package com.indiaexplorer.controller;

import com.indiaexplorer.model.Comment;
import com.indiaexplorer.model.Post;
import com.indiaexplorer.model.User;
import com.indiaexplorer.repository.CommentRepository;
import com.indiaexplorer.repository.PostRepository;
import com.indiaexplorer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Comment comment) {
        // 1. Verify the User exists
        User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify the Post exists
        Post post = postRepository.findById(comment.getPost().getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 3. Link them together
        comment.setUser(user);
        comment.setPost(post);

        // 4. Save and return
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }
}