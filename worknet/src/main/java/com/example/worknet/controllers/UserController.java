package com.example.worknet.controllers;


import com.example.worknet.dto.LoginUserDTO;
import com.example.worknet.dto.MessageDTO;
import com.example.worknet.dto.RegisterUserDTO;
import com.example.worknet.dto.UserDTO;
import com.example.worknet.entities.Like;
import com.example.worknet.entities.Message;
import com.example.worknet.entities.Post;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.security.JwtGenerator;
import com.example.worknet.services.PostService;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    
    

    @GetMapping("/login")
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
