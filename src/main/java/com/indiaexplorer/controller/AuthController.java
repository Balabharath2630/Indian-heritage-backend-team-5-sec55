package com.indiaexplorer.controller;

import com.indiaexplorer.model.User;
import com.indiaexplorer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") 
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("status", "error");
            response.put("message", "Email is already registered!");
            return ResponseEntity.status(400).body(response);
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole().name())) {
            String MASTER_KEY = "INDIA_ADMIN_2026"; 
            if (user.getAdminKey() == null || !user.getAdminKey().equals(MASTER_KEY)) {
                response.put("status", "error");
                response.put("message", "Invalid Admin Passcode! Access Denied.");
                return ResponseEntity.status(403).body(response);
            }
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!encoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("user", user);
        return res;
    }

    @GetMapping("/users")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * ✅ NEW: Admin Power to remove users
     * This endpoint handles the removal of test or unauthorized accounts.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!userRepository.existsById(id)) {
                response.put("message", "User not found.");
                return ResponseEntity.status(404).body(response);
            }

            userRepository.deleteById(id);
            response.put("message", "User deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}