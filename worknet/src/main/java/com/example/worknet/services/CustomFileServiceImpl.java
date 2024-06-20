package com.example.worknet.services;

import com.example.worknet.entities.CustomFile;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.CustomFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomFileServiceImpl implements CustomFileService {

    @Autowired
    private CustomFileRepository customFileRepository;

    public CustomFile getCustomFileById(Long id) {
        Optional<CustomFile> customFile = customFileRepository.findById(id);

        return customFile.orElse(null);
    }

    public CustomFile getCustomFileByName(String fileName) {
        Optional<CustomFile> customFile = customFileRepository.findCustomFileByFileNameContainingIgnoreCase(fileName);

        return customFile.orElse(null);
    }

    public List<CustomFile> getAllCustomFiles(){
        return customFileRepository.findAll();
    }

    public CustomFile addCustomFile(CustomFile customFile){
        return customFileRepository.save(customFile);
    }

    public CustomFile updateCustomFile(Long id, CustomFile customFile) {
        Optional<CustomFile> customFileOptional = customFileRepository.findById(id);
        if (customFileOptional.isPresent()) {
            CustomFile existingCustomFile = customFileOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(customFile, existingCustomFile);

            return customFileRepository.save(existingCustomFile);
        }

        return null;
    }

    public void deleteCustomFile(Long id){
        customFileRepository.deleteById(id);
    }

    public String savePostFile(MultipartFile file, Long postId) throws IOException {

        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null) {
            int lastIndex = originalFileName.lastIndexOf(".");
            if (lastIndex != -1) {
                fileExtension = originalFileName.substring(lastIndex);
            }
        }

        // Generate a unique filename or use customName
        String fileName = originalFileName == null ? UUID.randomUUID().toString() + fileExtension : "post_" + postId + "_" + originalFileName;

        // Path to save the uploaded file
        Path path = Paths.get("../FileStorage/posts/" + fileName);

        // Save the file locally
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(file.getBytes());
        }

        return fileName;
    }

    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null) {
            int lastIndex = originalFileName.lastIndexOf(".");
            if (lastIndex != -1) {
                fileExtension = originalFileName.substring(lastIndex);
            }
        }

        // Generate a unique filename or use the user's ID as the filename
        String fileName = userId + "_profile_picture" + fileExtension;

        // Path to save the profile picture
        Path path = Paths.get("../FileStorage/images/" + fileName);

        // Save the file locally
        Files.copy(file.getInputStream(), path);

        return fileName;
    }

    public void deleteProfilePicture(String fileName) throws IOException {
        // Path to the existing profile picture
        Path path = Paths.get("../FileStorage/images/" + fileName);
        // Delete the file
        Files.deleteIfExists(path);
    }


}
