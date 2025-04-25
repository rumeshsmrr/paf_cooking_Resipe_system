package com.cookingshare.backend.controller;

import com.cookingshare.backend.dto.ResponseMessageDTO;
import com.cookingshare.backend.model.AppUser;
import com.cookingshare.backend.repository.UserRepository;
import com.cookingshare.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
        System.out.println("Registering user: " + user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ResponseMessageDTO("Email already registered."));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider("LOCAL");
        user.setRole("LEARNER");

        AppUser savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // ✅ Login and return JWT
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        Optional<AppUser> user = userRepository.findByEmail(loginData.get("email"));

        if (user.isEmpty() || !passwordEncoder.matches(loginData.get("password"), user.get().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessageDTO("Invalid credentials"));
        }

        String token = jwtUtil.generateToken(
                user.get().getId(),
                user.get().getEmail(),
                user.get().getRole()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
            "id", user.get().getId(),
            "email", user.get().getEmail(),
            "name", user.get().getName(),
            "role", user.get().getRole()
        ));
        return ResponseEntity.ok(response);

    }

    // ✅ Get current user using token
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@RequestHeader("Authorization") String authHeader) {
        System.out.println("Fetching logged-in user from token: " + authHeader);
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserIdFromToken(token);

            return userRepository.findById(userId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ResponseMessageDTO("User not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessageDTO("Invalid or expired token"));
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AppUser updatedUser) {
        System.out.println("Updating user profile: " + updatedUser.getEmail());
        try {
            String token  = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserIdFromToken(token);

            return userRepository.findById(userId)
                .<ResponseEntity<?>>map(existingUser -> {
                    existingUser.setName(updatedUser.getName());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    AppUser savedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(savedUser);
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessageDTO("User not found")));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessageDTO("Invalid or expired token"));
        }
    }

    // ✅ Delete user account, same user and admin can delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        System.out.println("Deleting user account");
        try {
            String token  = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserIdFromToken(token);

            return userRepository.findById(userId)
                .<ResponseEntity<?>>map(existingUser -> {
                    userRepository.delete(existingUser);
                    return ResponseEntity.ok(new ResponseMessageDTO("User deleted successfully"));
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessageDTO("User not found")));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessageDTO("Invalid or expired token"));
        }
    }

    
}
