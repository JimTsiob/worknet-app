package com.example.worknet.controllers;

import com.example.worknet.dto.CertificationDTO;
import com.example.worknet.entities.Certification;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certifications")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllCertifications() {
        List<Certification> certifications = certificationService.getAllCertifications();

        List<CertificationDTO> certificationDTOList =  certifications.stream()
                .map(certification -> modelMapper.map(certification, CertificationDTO.class))
                .toList();

        return ResponseEntity.ok(certificationDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificationById(@PathVariable Long id) {
        Certification certification = certificationService.getCertificationById(id);

        CertificationDTO certificationDTO =  modelMapper.map(certification, CertificationDTO.class);
        if (certificationDTO != null){
            return ResponseEntity.ok(certificationDTO);
        }else{
            String errorMessage = "Certification with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addCertification(@RequestBody CertificationDTO certificationDTO) {
        try {
            Certification certification = modelMapper.map(certificationDTO, Certification.class);

            certificationService.addCertification(certification);

            return ResponseEntity.status(HttpStatus.CREATED).body("Certification added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add certification: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCertification(@PathVariable Long id, @RequestBody CertificationDTO certificationDTO) {
        try {

            Certification existingCertification = certificationService.getCertificationById(id);
            if (existingCertification == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Certification with ID " + id + " does not exist.");
            }

            modelMapper.map(certificationDTO, existingCertification);

            certificationService.updateCertification(id, existingCertification);

            return ResponseEntity.ok("Certification updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update certification with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEducation(@PathVariable Long id) {
        try {
            // Check if the education with the given id exists
            Certification existingCertification = certificationService.getCertificationById(id);
            if (existingCertification == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Certification with ID " + id + " does not exist.");
            }

            certificationService.deleteCertification(id);

            return ResponseEntity.ok("Certification deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete certification: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
