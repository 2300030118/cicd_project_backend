package klu.controller;

import klu.model.User;
import klu.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPasswordHash().equals(password)) {
            return ResponseEntity.ok(Map.of("message", "login ok", "userId", user.get().getId()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "invalid credentials"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return ResponseEntity.ok(Map.of("message", "if exists, mail sent"));
        String token = UUID.randomUUID().toString();
        User u = user.get();
        u.setResetToken(token);
        u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");
        Optional<User> user = userRepository.findByResetToken(token);
        if (user.isEmpty()) return ResponseEntity.status(400).body(Map.of("message", "invalid token"));
        User u = user.get();
        if (u.getResetTokenExpiry() == null || u.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body(Map.of("message", "token expired"));
        }
        u.setPasswordHash(newPassword);
        u.setResetToken(null);
        u.setResetTokenExpiry(null);
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "password updated"));
    }
}





