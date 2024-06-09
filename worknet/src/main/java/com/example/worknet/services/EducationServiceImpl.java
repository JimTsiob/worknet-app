package com.example.worknet.services;

import com.example.worknet.entities.Education;
import com.example.worknet.entities.User;
import com.example.worknet.repositories.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EducationServiceImpl implements EducationService {

    @Autowired
    private EducationRepository educationRepository;

    public List<Education> getAllEducations() {
        return educationRepository.findAll();
    }

    public Education addEducation(Education education, User user) {

        user.getEducations().add(education);

        return educationRepository.save(education);
    }

    public Education getEducationById(Long id) {
        Optional<Education> optionalEducation = educationRepository.findById(id);
        return optionalEducation.orElse(null);
    }

    public Education updateEducation(Long id, Education education) {
        if (educationRepository.existsById(id)) {
            education.setId(id); // Ensure the ID is set for update
            return educationRepository.save(education);
        }
        return null;
    }

    public void deleteEducation(Long id) {
        educationRepository.deleteById(id);
    }

    public boolean educationEquals(Education education1, Education education2) {
        if (education1.getSchool().trim().equalsIgnoreCase(education2.getSchool().trim()) &&
            education1.getDegree().trim().equalsIgnoreCase(education2.getDegree().trim()) &&
            education1.getFieldOfStudy().trim().equalsIgnoreCase(education2.getFieldOfStudy().trim()) &&
            education1.getStartDate().equals(education2.getStartDate()) &&
            education1.getEndDate().equals(education2.getEndDate())) {
            return true;
        }

        return false;
    }
}