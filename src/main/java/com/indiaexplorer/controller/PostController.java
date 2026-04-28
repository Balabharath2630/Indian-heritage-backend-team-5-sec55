package com.indiaexplorer.controller;

import com.indiaexplorer.model.Post;
import com.indiaexplorer.model.User;
import com.indiaexplorer.repository.UserRepository;
import com.indiaexplorer.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
// ❌ Removed @CrossOrigin (important fix)
public class PostController {

    private final PostService postService;
    
    @Autowired
    private UserRepository userRepository; // ✅ Added to link User details to Posts

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // GET all posts
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    // POST a new thread
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        // 1. Check if user data was sent
        if (post.getUser() == null || post.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("Error: User ID is required to post.");
        }

        // 2. Fetch the full User from DB
        User author = userRepository.findById(post.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Attach the full User object
        post.setUser(author);

        // 4. Save the post
        Post savedPost = postService.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/{id}/like")
    public Post likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok("Post deleted successfully");
    }
}