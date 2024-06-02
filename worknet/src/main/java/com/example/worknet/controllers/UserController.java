package com.example.worknet.controllers;


import com.example.worknet.dto.*;
import com.example.worknet.entities.Job;
import com.example.worknet.entities.Message;
import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;
import com.example.worknet.entities.View;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.recommendationSystem.RecommendationSystem;
import com.example.worknet.security.JwtGenerator;
import com.example.worknet.services.JobService;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final StrictModelMapper modelMapper = new StrictModelMapper();

    @Autowired
    private final JwtGenerator jwtGenerator;

    public UserController(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }


    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserDTO> userDTOList =  users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return ResponseEntity.ok(userDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        UserDTO userDTO =  modelMapper.map(user, UserDTO.class);
        if (userDTO != null){
            return ResponseEntity.ok(userDTO);
        }else{
            String errorMessage = "User with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    // user has 4 options, they can input the full name (eg. Makis Papadopoulos) ,
    // full name in reverse (Papadopoulos Makis),
    // the first name only (Makis) or the last name only (Papadopoulos)
    // this method searches both for first and last name. Anything else will fail.
    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam String name) {
        List<User> users = userService.searchUser(name);

        if (users.isEmpty()){
            return new ResponseEntity<>("No users were found.", HttpStatus.NOT_FOUND);
        }

        List<UserDTO> userDTOList =  users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return ResponseEntity.ok(userDTOList);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> recommendJobs(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found.");
        }

        List<User> connections = user.getConnections();
        List<Skill> userSkills = user.getSkills();
        List<Job> jobs = jobService.getAllJobs();

        // recommendation by matrix factorization on user's connections
        if (!connections.isEmpty()) {
            List<View> connectionViews = new ArrayList<>();
            for (User connection : connections) {
                connectionViews.addAll(connection.getViews());
            }

            HashSet<Job> noDupesJobs = new HashSet<>(); // remove duplicates
            for (View connectionView : connectionViews) {
                noDupesJobs.add(jobService.getJobById(connectionView.getJob().getId()));

            }

            List<Job> connectionJobs = new ArrayList<>(noDupesJobs);

            RecommendationSystem recommendationSystem = new RecommendationSystem();

            recommendationSystem.createInteractionMatrix(connections, connectionJobs);

            connections.add(user); // add this to get results later on.

            recommendationSystem.matrixFactorization(connections.size(), connectionJobs.size(), 10);

            List<Job> recommendedJobs = recommendationSystem.getRecommendedJobs(user, connectionJobs);

            // turn them to DTOs and return
            List<SmallJobDTO> recommendedJobDTOs = new ArrayList<>();
            for (Job recommendedJob : recommendedJobs) {
                SmallJobDTO recommendedJobDTO = modelMapper.map(recommendedJob, SmallJobDTO.class);
                recommendedJobDTOs.add(recommendedJobDTO);
            }

            return ResponseEntity.ok(recommendedJobDTOs);
        }

        // recommendation by user skills 
        if (!userSkills.isEmpty()) {

            RecommendationSystem recommendationSystem = new RecommendationSystem();

            HashSet<Job> recommendedJobs = recommendationSystem.recommendJobsBySkill(user, jobs);
            
            List<SmallJobDTO> recommendedJobDTOs = new ArrayList<>();

            for (Job recommendedJob : recommendedJobs) {
                SmallJobDTO recommendedJobDTO = modelMapper.map(recommendedJob, SmallJobDTO.class);
                recommendedJobDTOs.add(recommendedJobDTO);
            }

            return ResponseEntity.ok(recommendedJobDTOs);
        }

        return new ResponseEntity<>("Add connections or skills to get recommendations for job posts!", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);

            userService.addUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add user: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/addConnection")
    public ResponseEntity<?> addConnection(@RequestParam Long userId,
                                           @RequestParam Long connectionId) {
        try {
            userService.addConnection(userId, connectionId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Connection added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add connection: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/addSkill")
    public ResponseEntity<?> addSkill(@RequestParam Long userId,
                                @RequestParam String skillName) {
        try {
            userService.addSkill(userId, skillName);
            return ResponseEntity.status(HttpStatus.CREATED).body("Skill added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add skill: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
    

    @PostMapping("/applyToJob")
    public ResponseEntity<?> applyToJob(@RequestParam Long userId,
                                           @RequestParam Long jobId) {
        try {
            userService.applyToJob(userId, jobId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Applied to job successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to apply to job: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/addLike")
    public ResponseEntity<?> addLike(@RequestParam Long userId,
                                 @RequestParam Long postId) {
        try {
            userService.addLike(userId, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post liked successfully.");
        } catch (Exception e) {
            String errorMessage = "Failed to like post: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO) {

        try {
            Message message = modelMapper.map(messageDTO, Message.class);
            userService.sendMessage(message);
            return ResponseEntity.status(HttpStatus.CREATED).body("Message sent successfully.");
        } catch (Exception e) {
            String errorMessage = "Failed to send message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/addView")
    public ResponseEntity<?> addView(@RequestParam Long userId,
                                     @RequestParam Long jobId) {
        try {
            userService.addView(userId, jobId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Viewed job successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to view job: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
    
    
    // ---------------------------------------------- AUTHENTICATION ----------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDTO loginUserDTO) {
        User user = userService.getUserByEmail(loginUserDTO.getEmail());
        if (user == null){
            return new ResponseEntity<>("User not found. Try other credentials.", HttpStatus.NOT_FOUND);
        }

        String token = jwtGenerator.generateToken(loginUserDTO);

        user.setJwtToken(token);
        userService.updateUser(user.getId(), user);

        return new ResponseEntity<>("User logged in successfully. Bearer " + token, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        User user = userService.getUserByEmail(registerUserDTO.getEmail());
        if (user != null) {
            return new ResponseEntity<>("user with email: " + user.getEmail() + " already exists.", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setFirstName(registerUserDTO.getFirstName());
        newUser.setLastName(registerUserDTO.getLastName());
        newUser.setPhoneNumber(registerUserDTO.getPhoneNumber());

        userService.addUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LoginUserDTO loginUserDTO) {
        User user = userService.getUserByEmail(loginUserDTO.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
        }

        user.setJwtToken(null);
        userService.updateUser(user.getId(), user);

        return new ResponseEntity<>("User logged out successfully.", HttpStatus.OK);
    }

    // ---------------------------------------------- END OF AUTHENTICATION ----------------------------------------------
    

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {

            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User with ID " + id + " does not exist.");
            }

            modelMapper.map(userDTO, existingUser);

            userService.updateUser(id, existingUser);

            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update user with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {

            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User with ID " + id + " does not exist.");
            }

            userService.deleteUser(id);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete user: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // endpoint for removing connection for user deletion
    // and if user wants to remove a connection.
    @DeleteMapping("/removeConnection")
    public ResponseEntity<?> removeConnection(@RequestParam Long userId,
                                              @RequestParam Long connectionId) {
        try {
            userService.removeConnection(userId, connectionId);
            return ResponseEntity.status(HttpStatus.OK).body("Connection removed successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to remove connection: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // endpoint for job deletion and in case a user wants to remove their job application.
    @DeleteMapping("/removeApplicationFromJob")
    public ResponseEntity<?> removeApplicationFromJob(@RequestParam Long userId,
                                              @RequestParam Long jobId) {
        try {
            userService.removeApplicationFromJob(userId, jobId);
            return ResponseEntity.status(HttpStatus.OK).body("Job application removed successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to remove job application: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // endpoint to remove likes in case user wants to remove the like from the post.
    @DeleteMapping("/removeLike")
    public ResponseEntity<?> removeLike(@RequestParam Long userId,
                                        @RequestParam Long postId,
                                        @RequestParam Long likeId) {
        try {
            userService.removeLike(userId, postId, likeId);
            return ResponseEntity.status(HttpStatus.OK).body("Like removed successfully.");
        } catch (Exception e) {
            String errorMessage = "Failed to remove like: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
