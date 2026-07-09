package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.exception.ResourceNotFoundException;
import com.eventmate.model.User;
import com.eventmate.repository.UserRepository;
import com.eventmate.service.ImageStorageService;
import com.eventmate.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public UserServiceImpl(UserRepository userRepository, ImageStorageService imageStorageService) {
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public User getUserProfile(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateUserProfile(String id, String name, MultipartFile profilePicture) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (name != null) user.setName(name);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                String oldUrl = user.getProfilePictureUrl();
                user.setProfilePictureUrl(imageStorageService.upload(profilePicture));
                
                if (oldUrl != null && oldUrl.startsWith("http")) {
                    try {
                        imageStorageService.delete(oldUrl);
                    } catch (Exception ignored) {}
                }
            } catch (IOException ex) {
                throw new BadRequestException("Could not process profile picture to cloud.");
            }
        }

        User updatedUser = userRepository.save(user);
        updatedUser.setPassword(null);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public User toggleBlockUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBlocked(!user.isBlocked());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getProfilePictureUrl() != null && user.getProfilePictureUrl().startsWith("http")) {
            try {
                imageStorageService.delete(user.getProfilePictureUrl());
            } catch (Exception ignored) {}
        }
        userRepository.deleteById(id);
    }
}
