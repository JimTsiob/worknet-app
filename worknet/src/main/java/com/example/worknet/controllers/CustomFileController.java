package com.example.worknet.controllers;

import com.example.worknet.dto.CustomFileDTO;
import com.example.worknet.entities.CustomFile;
import com.example.worknet.entities.Post;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.CustomFileService;
import com.example.worknet.services.PostService;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customFiles")
public class CustomFileController {

    @Autowired
    private CustomFileService customFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllCustomFiles() {
        List<CustomFile> customFiles = customFileService.getAllCustomFiles();

        List<CustomFileDTO> customFileDTOList =  customFiles.stream()
                .map(customFile -> modelMapper.map(customFile, CustomFileDTO.class))
                .toList();

        return ResponseEntity.ok(customFileDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomFileById(@PathVariable Long id) {
        CustomFile customFile = customFileService.getCustomFileById(id);

        CustomFileDTO customFileDTO =  modelMapper.map(customFile, CustomFileDTO.class);
        if (customFileDTO != null){
            return ResponseEntity.ok(customFileDTO);
        }else{
            String errorMessage = "Custom file with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addCustomFile(@RequestBody CustomFileDTO customFileDTO) {
        try {
            CustomFile customFile = modelMapper.map(customFileDTO, CustomFile.class);

            customFileService.addCustomFile(customFile);

            return ResponseEntity.status(HttpStatus.CREATED).body("Custom file added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add custom file: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/uploadPostFiles")
    public String uploadPostFiles(@RequestParam("files") List<MultipartFile> files,
                                  @RequestParam("userId") Long userId,
                                  @RequestParam("postId") Long postId) {

        if (files.isEmpty()) {
            return "Please select files to upload.";
        }

        try {
            List<CustomFile> filesToBeAdded = new ArrayList<>();
            User user = userService.getUserById(userId);
            Post post = postService.getPostById(postId);

            for (MultipartFile file : files) {
                // Process and save the file
                String fileName = customFileService.savePostFile(file,postId);

                // compress data for DB
                byte[] fileBytes = file.getBytes();
                byte[] compressedBytes = customFileService.compressData(fileBytes);

                // String inputStreamString = customFileService.encodeInputStreamToBase64(file.getInputStream());

                // Create and save CustomFile entity
                CustomFile customFile = new CustomFile();
                customFile.setFileName(fileName);
                customFile.setContentType(file.getContentType());
                customFile.setSize(file.getSize());
                customFile.setFileContent(compressedBytes);

                // Set the associated User and Post
                customFile.setUser(user);
                customFile.setPost(post);

                // save files to the user's list, so they can be related to the user
                filesToBeAdded.add(customFile);

                // Save the CustomFile entity to your database using JPA or Hibernate
                customFileService.addCustomFile(customFile);
            }

            // Relate the newly added files to the user and post.
            user.setFiles(filesToBeAdded);
            userService.updateUser(userId, user);

            post.setCustomFiles(filesToBeAdded);
            postService.updatePost(postId, post);

            return "Files uploaded successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload files.";
        }
    }

    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              @RequestParam("userId") Long userId) {

        if (file.isEmpty()) {
            return "Please select a file to upload.";
        }

        try {

            String imageName = userId + "_profile_picture.";
            CustomFile existingCustomFile = customFileService.getCustomFileByName(imageName);
            if (existingCustomFile != null) {
                // delete existing entry in the database
                customFileService.deleteCustomFile(existingCustomFile.getId());
            }

            // Update the user's profile picture reference
            User user = userService.getUserById(userId);
            if (user != null) {
                // Delete the old profile picture if it exists locally
                if (user.getProfilePicture() != null) {
                    customFileService.deleteProfilePicture(user.getProfilePicture());
                }

                // compress data for DB
                byte[] fileBytes = file.getBytes();
                byte[] compressedBytes = customFileService.compressData(fileBytes);

                String fileName = customFileService.saveProfilePicture(file, userId);

                CustomFile customFile = new CustomFile();
                customFile.setFileContent(compressedBytes);
                customFile.setFileName(fileName);
                customFile.setContentType(file.getContentType());
                customFile.setSize(file.getSize());
                customFile.setUser(userService.getUserById(userId));
                customFileService.addCustomFile(customFile);

                // Set the new profile picture
                user.setProfilePicture(fileName);
                userService.updateUser(userId, user);
                return "Profile picture uploaded successfully.";
            } else {
                return "User not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload profile picture.";
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomFile(@PathVariable Long id, @RequestBody CustomFileDTO customFileDTO) {
        try {

            CustomFile existingCustomFile = customFileService.getCustomFileById(id);
            if (existingCustomFile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Custom file with ID " + id + " does not exist.");
            }

            modelMapper.map(customFileDTO, existingCustomFile);

            customFileService.updateCustomFile(id, existingCustomFile);

            return ResponseEntity.ok("Custom file updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update custom file with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomFile(@PathVariable Long id) {
        try {

            CustomFile existingCustomFile = customFileService.getCustomFileById(id);
            if (existingCustomFile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Custom file with ID " + id + " does not exist.");
            }

            customFileService.deleteCustomFile(id);

            return ResponseEntity.ok("Custom file deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete custom file: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
