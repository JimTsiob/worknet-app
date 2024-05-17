package com.example.worknet.services;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.Like;
import com.example.worknet.entities.Post;
import com.example.worknet.entities.User;
import com.example.worknet.entities.Message;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.JobRepository;
import com.example.worknet.repositories.LikeRepository;
import com.example.worknet.repositories.PostRepository;
import com.example.worknet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    private final StrictModelMapper strictModelMapper = new StrictModelMapper();

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElse(null);
    }

    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.orElse(null);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            strictModelMapper.map(user, existingUser);

            return userRepository.save(existingUser);
        }

        return null;
    }

    public void deleteUser(Long id){

        // Remove the user from the connections list of all other users
        // and then delete user from the DB as well.
        for (User user : userRepository.findAll()) {
            removeConnection(user.getId(), id);
        }

        // also remove from the job post
        for (User user : userRepository.findAll()) {
            removeConnection(user.getId(), id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            return null;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public void addConnection(Long userId, Long connectionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User connection = userRepository.findById(connectionId).orElseThrow(() -> new RuntimeException("Connection not found"));

        user.getConnections().add(connection);
        connection.getConnections().add(user);

        userRepository.save(user);
        userRepository.save(connection);
    }

    public void removeConnection(Long userId, Long connectionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User connection = userRepository.findById(connectionId).orElseThrow(() -> new RuntimeException("Connection not found"));

        user.getConnections().remove(connection);
        connection.getConnections().remove(user);

        userRepository.save(user);
        userRepository.save(connection);
    }

    public void applyToJob(Long userId, Long jobId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        user.getAppliedJobs().add(job);
        job.getInterestedUsers().add(user);

        userRepository.save(user);
        jobRepository.save(job);
    }

    public void removeApplicationFromJob(Long userId, Long jobId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        user.getAppliedJobs().remove(job);
        job.getInterestedUsers().remove(user);

        userRepository.save(user);
        jobRepository.save(job);
    }
    
    public void addLike(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        user.getLikes().add(like);
        post.getLikes().add(like);

        userRepository.save(user);
        postRepository.save(post);
    }

    public void removeLike(Long userId, Long postId, Long likeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Like like = likeRepository.findById(likeId).orElseThrow(() -> new RuntimeException("Like not found"));
        
        user.getLikes().remove(like);
        post.getLikes().remove(like);

        userRepository.save(user);
        postRepository.save(post);
    }

    public void addMessage(Long recipientId, Message message) {
        User recipient = userRepository.findById(recipientId).orElseThrow(() -> new RuntimeException("Recipient not found"));

        recipient.getMessages().add(message);
        userRepository.save(recipient);
    }
}
