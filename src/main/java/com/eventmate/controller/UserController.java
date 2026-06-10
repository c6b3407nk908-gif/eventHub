package com.eventmate.controller;

import com.eventmate.model.User;
import com.eventmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(null); // Don't return password
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable String id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "contact", required = false) String contact,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        if (name != null) user.setName(name);
        if (contact != null) user.setContact(contact);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                byte[] bytes = profilePicture.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                String contentType = profilePicture.getContentType();
                if (contentType == null) {
                    contentType = "image/jpeg";
                }
                String dataUrl = "data:" + contentType + ";base64," + base64Image;
                user.setProfilePictureUrl(dataUrl);
            } catch (IOException ex) {
                return ResponseEntity.badRequest().body("Could not process profile picture.");
            }
        }

        User updatedUser = userRepository.save(user);
        updatedUser.setPassword(null);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        java.util.List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> toggleBlockUser(@PathVariable String id) {
        return userRepository.findById(id).map(user -> {
            user.setBlocked(!user.isBlocked());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
