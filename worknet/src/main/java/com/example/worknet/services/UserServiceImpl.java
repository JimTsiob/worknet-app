package com.example.worknet.services;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.Like;
import com.example.worknet.entities.Post;
import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;
import com.example.worknet.entities.View;
import com.example.worknet.entities.Message;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.*;
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
    @Autowired
    private MessageRepository messageRepository;

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

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // remove messages too
        for (Message message : user.getMessages()){
            removeMessage(id, message.getId());
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

        likeRepository.delete(like);
    }

    public void sendMessage(Message message) {

        messageRepository.save(message);

        for (User user: message.getUsers()){
            User messageUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
            messageUser.getMessages().add(message);
            userRepository.save(messageUser);
        }
    }

    public void removeMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));

        // completely delete messages to maintain best practice
        // and save space in the database.

        for (User messageuser : message.getUsers()) {
            messageuser.getMessages().remove(message);
            userRepository.save(messageuser);
        }

        message.getUsers().clear();

        messageRepository.delete(message);
    }

    public void addView(Long userId, Long jobId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        View view = new View();
        view.setUser(user);
        view.setJob(job);

        user.getViews().add(view);

        userRepository.save(user);
        jobRepository.save(job);
    }

    public void addSkill(Long userId, String skillName){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Skill skill = new Skill();
        skill.setName(skillName);
        skill.setUser(user);

        user.getSkills().add(skill);

        userRepository.save(user);
    }

    public List<User> searchUser(String name) {
        String[] names = name.split(" ");
        if (names.length == 2) {
            List<User> firstLastName =  userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(names[0], names[1]);
            List<User> lastFirstName = userRepository.findByLastNameContainingIgnoreCaseAndFirstNameContainingIgnoreCase(names[0],names[1]);

            if (!firstLastName.isEmpty()){
                return firstLastName;
            }else{
                return lastFirstName;
            }
        } else {
            // Handle cases where the fullName doesn't split into exactly two parts
            return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
        }
    }
}
