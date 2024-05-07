package com.example.worknet.services;

import com.example.worknet.entities.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    User addUser(User user);
    User updateUser(Long id,User user);
    void deleteUser(Long id);
}
