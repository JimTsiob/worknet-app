package com.example.worknet.controllers;


import com.example.worknet.dto.LoginUserDTO;
import com.example.worknet.dto.RegisterUserDTO;
import com.example.worknet.dto.UserDTO;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.security.JwtGenerator;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDTO loginUserDTO) {
        User user = userService.getUserByEmail(loginUserDTO.getEmail());
        if (user == null){
            return new ResponseEntity<>("User not found. Try other credentials.", HttpStatus.NOT_FOUND);
        }

        String token = jwtGenerator.generateToken(loginUserDTO);

        return new ResponseEntity<>("Bearer " + token, HttpStatus.OK);
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

            // Assuming userService.deleteUser(id) deletes the user from the database
            userService.deleteUser(id);

            // Optionally, you can return a success message
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete user: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
