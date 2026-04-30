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

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*") // safer for deployment
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // ================= CREATE COMMENT =================
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Comment comment) {

        User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(comment.getPost().getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        comment.setUser(user);
        comment.setPost(post);

        // ✅ Handle reply (if parent exists)
        if (comment.getParentComment() != null) {
            Comment parent = commentRepository.findById(comment.getParentComment().getId())
                    .orElse(null);
            comment.setParentComment(parent);
        }

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }

    // ================= LIKE COMMENT =================
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id, @RequestParam Long userId) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // ✅ Prevent multiple likes
        if (comment.getLikedUserIds().contains(userId)) {
            return ResponseEntity.badRequest().body("Already liked");
        }

        comment.getLikedUserIds().add(userId);
        comment.setLikes(comment.getLikes() + 1);

        commentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }

    // ================= EDIT COMMENT =================
    @PutMapping("/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody Comment updatedComment) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // ✅ Only owner can edit
        if (!comment.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Not allowed to edit");
        }

        comment.setText(updatedComment.getText());
        commentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }

    // ================= DELETE COMMENT =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @RequestParam Long userId) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        // ✅ Admin OR owner can delete
        boolean isOwner = comment.getUser().getId().equals(userId);
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Not allowed to delete");
        }

        commentRepository.deleteById(id);

        return ResponseEntity.ok("Comment deleted");
    }
}