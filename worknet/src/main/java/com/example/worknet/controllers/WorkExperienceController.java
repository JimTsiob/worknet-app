package com.example.worknet.controllers;

import com.example.worknet.dto.WorkExperienceDTO;
import com.example.worknet.entities.User;
import com.example.worknet.entities.WorkExperience;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.UserService;
import com.example.worknet.services.WorkExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workexperiences")
public class WorkExperienceController {

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private UserService userService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();

    @GetMapping("/")
    public ResponseEntity<?> getAllWorkExperiences() {
        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperiences();

        List<WorkExperienceDTO> workExperienceDTOList =  workExperiences.stream()
                .map(workExperience -> modelMapper.map(workExperience, WorkExperienceDTO.class))
                .toList();

        return ResponseEntity.ok(workExperienceDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkExperienceById(@PathVariable Long id) {
        WorkExperience workExperience = workExperienceService.getWorkExperienceById(id);

        WorkExperienceDTO workExperienceDTO =  modelMapper.map(workExperience, WorkExperienceDTO.class);
        if (workExperienceDTO != null){
            return ResponseEntity.ok(workExperienceDTO);
        }else{
            String errorMessage = "Work experience with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addWorkExperience(@RequestBody WorkExperienceDTO workExperienceDTO, @RequestParam String email) {
        try {
            WorkExperience workExperience = modelMapper.map(workExperienceDTO, WorkExperience.class);
            User user = userService.getUserByEmail(email);
            List<WorkExperience> workExperiences = user.getWorkExperiences();

            // do not allow same work experience to be added twice
            for (WorkExperience we: workExperiences) {
                if (workExperienceService.equalsWorkExperience(workExperience,we)){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot add same work experience twice.");
                }
            }

            workExperienceService.addWorkExperience(workExperience, user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Work experience added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add work experience: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkExperience(@PathVariable Long id, @RequestBody WorkExperienceDTO workExperienceDTO) {
        try {

            WorkExperience existingWorkExperience = workExperienceService.getWorkExperienceById(id);
            if (existingWorkExperience == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Work experience with ID " + id + " does not exist.");
            }

            modelMapper.map(workExperienceDTO, existingWorkExperience);

            workExperienceService.updateWorkExperience(id, existingWorkExperience);

            return ResponseEntity.ok("Work experience updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update work experience with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkExperience(@PathVariable Long id) {
        try {

            WorkExperience existingWorkExperience = workExperienceService.getWorkExperienceById(id);
            if (existingWorkExperience == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Work experience with ID " + id + " does not exist.");
            }

            workExperienceService.deleteWorkExperience(id);

            return ResponseEntity.ok("Work experience deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete work experience: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
