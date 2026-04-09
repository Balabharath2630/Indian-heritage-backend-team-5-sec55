package com.indiaexplorer.service;

import com.indiaexplorer.model.User;
import com.indiaexplorer.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    // Final ensures the repository is required and immutable
    private final UserRepository userRepository;

    // Manual Constructor Injection (Fixes the Line 13 error)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}