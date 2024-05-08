package com.example.worknet.controllers;

import com.example.worknet.dto.CustomFileDTO;
import com.example.worknet.dto.EducationDTO;
import com.example.worknet.entities.CustomFile;
import com.example.worknet.entities.Education;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.CustomFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customFiles")
public class CustomFileController {

    @Autowired
    private CustomFileService customFileService;

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
