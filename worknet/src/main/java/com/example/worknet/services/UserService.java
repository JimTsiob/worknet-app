package com.example.worknet.services;

import com.example.worknet.entities.Message;
import com.example.worknet.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User addUser(User user);
    User updateUser(Long id,User user);
    void deleteUser(Long id);
    UserDetails loadUserByUsername(String email);
    void addConnection(Long userId, Long connectionId);
    void removeConnection(Long userId, Long connectionId);
    void applyToJob(Long userId, Long jobId);
    void removeApplicationFromJob(Long userId, Long jobId);
    void addLike(Long userId, Long postId);
    void removeLike(Long userId, Long postId, Long likeId);
    void sendMessage(Message message);
    void removeMessage(Long userId, Long messageId);
    void addView(Long userId, Long jobId);
    void addSkill(Long userId, String skillName);
    List<User> searchUser(String name);
}
