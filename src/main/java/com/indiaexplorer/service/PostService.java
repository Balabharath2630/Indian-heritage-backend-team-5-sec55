package com.indiaexplorer.service;

import com.indiaexplorer.model.Post;
import com.indiaexplorer.repository.PostRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post likePost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            // Correct way to increment and save
            int currentLikes = post.getLikes();
            post.setLikes(currentLikes + 1);
            return postRepository.save(post);
        }
        return null;
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}