package com.indiaexplorer.controller;

import com.indiaexplorer.model.User;
import com.indiaexplorer.repository.UserRepository;
import com.indiaexplorer.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
// ❌ REMOVED @CrossOrigin (important fix)
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Value("${MY_ADMIN_MASTER_KEY:INDIA_ADMIN_2026}")
    private String masterAdminKey;

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    // --- OTP METHODS ---

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        otpStorage.put(email, otp);

        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + email));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error sending email: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String userOtp = request.get("otp");

        String serverOtp = otpStorage.get(email);

        if (serverOtp != null && serverOtp.equals(userOtp)) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "OTP Verified!"));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid or expired OTP"));
        }
    }

    // --- REGISTRATION & LOGIN ---

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("status", "error");
            response.put("message", "Email is already registered!");
            return ResponseEntity.status(400).body(response);
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole().name())) {
            if (user.getAdminKey() == null || !user.getAdminKey().equals(masterAdminKey)) {
                response.put("status", "error");
                response.put("message", "Invalid Admin Passcode! Access Denied.");
                return ResponseEntity.status(403).body(response);
            }
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        otpStorage.remove(user.getEmail());

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

    // --- USER MANAGEMENT ---

    @GetMapping("/users")
    public List<User> getAll() {
        return userRepository.findAll();
    }

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