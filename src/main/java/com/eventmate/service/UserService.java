package com.eventmate.service;

import com.eventmate.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User getUserProfile(String id);
    User updateUserProfile(String id, String name, MultipartFile profilePicture);
    List<User> getAllUsers();
    User toggleBlockUser(String id);
    void deleteUser(String id);
}
