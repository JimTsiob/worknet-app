package com.example.worknet.controllers;


import com.example.worknet.dto.EducationDTO;
import com.example.worknet.entities.Education;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/educations")
public class EducationController {

    @Autowired
    private EducationService educationService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllEducations() {
        List<Education> educations = educationService.getAllEducations();

        List<EducationDTO> educationDTOList =  educations.stream()
                .map(education -> modelMapper.map(education, EducationDTO.class))
                .toList();

        return ResponseEntity.ok(educationDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEducationById(@PathVariable Long id) {
        Education education = educationService.getEducationById(id);

        EducationDTO educationDTO =  modelMapper.map(education, EducationDTO.class);
        if (educationDTO != null){
            return ResponseEntity.ok(educationDTO);
        }else{
            String errorMessage = "Education with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addEducation(@RequestBody EducationDTO educationDTO) {
        try {
            Education education = modelMapper.map(educationDTO, Education.class);

            educationService.addEducation(education);

            return ResponseEntity.status(HttpStatus.CREATED).body("Education added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add education: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEducation(@PathVariable Long id, @RequestBody EducationDTO educationDTO) {
        try {
            // Check if the user with the given id exists
            Education existingEducation = educationService.getEducationById(id);
            if (existingEducation == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Education with ID " + id + " does not exist.");
            }

            modelMapper.map(educationDTO, existingEducation);

            educationService.updateEducation(id, existingEducation);

            return ResponseEntity.ok("Education updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update education with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEducation(@PathVariable Long id) {
        try {
            // Check if the education with the given id exists
            Education existingEducation = educationService.getEducationById(id);
            if (existingEducation == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Education with ID " + id + " does not exist.");
            }

            educationService.deleteEducation(id);

            return ResponseEntity.ok("Education deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete education: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
