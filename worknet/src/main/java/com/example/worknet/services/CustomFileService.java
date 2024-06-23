package com.example.worknet.services;


import com.example.worknet.entities.CustomFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomFileService {
    CustomFile getCustomFileById(Long id);
    CustomFile getCustomFileByName(String fileName);
    List<CustomFile> getAllCustomFiles();
    CustomFile addCustomFile(CustomFile customFile);
    CustomFile updateCustomFile(Long id, CustomFile customFile);
    void deleteCustomFile(Long id);
    String savePostFile(MultipartFile file, Long postId) throws IOException;
    String saveProfilePicture(MultipartFile file, Long userId) throws IOException;
    void deleteProfilePicture(String fileName) throws IOException;
}
