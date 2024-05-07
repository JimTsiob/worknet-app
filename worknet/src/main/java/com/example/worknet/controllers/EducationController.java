package com.example.worknet.controllers;

import com.example.worknet.dto.EducationDTO;
import com.example.worknet.entities.Education;
import com.example.worknet.services.EducationService;
import com.example.worknet.services.EducationServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/educations")
public class EducationController {

    @Autowired
    private EducationService educationService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/")
    public ResponseEntity<List<EducationDTO>> getAllEducations() {
        List<Education> educations = educationService.getAllEducations();
        List<EducationDTO> educationDTOs = educations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(educationDTOs, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<EducationDTO> addEducation(@RequestBody EducationDTO educationDTO) {
        Education education = convertToEntity(educationDTO);
        Education createdEducation = educationService.addEducation(education);
        EducationDTO createdEducationDTO = convertToDTO(createdEducation);
        return new ResponseEntity<>(createdEducationDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long id) {
        Education education = educationService.getEducationById(id);
        if (education != null) {
            EducationDTO educationDTO = convertToDTO(education);
            return ResponseEntity.ok(educationDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationDTO> updateEducation(@PathVariable Long id, @RequestBody EducationDTO educationDTO) {
        Education educationToUpdate = convertToEntity(educationDTO);
        Education updatedEducation = educationService.updateEducation(id, educationToUpdate);
        if (updatedEducation != null) {
            EducationDTO updatedEducationDTO = convertToDTO(updatedEducation);
            return ResponseEntity.ok(updatedEducationDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Education entity to DTO
    private EducationDTO convertToDTO(Education education) {
        return modelMapper.map(education, EducationDTO.class);
    }

    // Helper method to convert EducationDTO to entity
    private Education convertToEntity(EducationDTO educationDTO) {
        return modelMapper.map(educationDTO, Education.class);
    }
}