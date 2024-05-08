package com.example.worknet.services;


import com.example.worknet.entities.CustomFile;
import com.example.worknet.entities.Job;

import java.util.List;

public interface CustomFileService {
    CustomFile getCustomFileById(Long id);
    List<CustomFile> getAllCustomFiles();
    CustomFile addCustomFile(CustomFile customFile);
    CustomFile updateCustomFile(Long id, CustomFile customFile);
    void deleteCustomFile(Long id);
}
