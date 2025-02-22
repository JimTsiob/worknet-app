package com.example.worknet.services;

import com.example.worknet.entities.User;
import com.example.worknet.entities.WorkExperience;
import com.example.worknet.repositories.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    public List<WorkExperience> getAllWorkExperiences() {
        return workExperienceRepository.findAll();
    }

    public WorkExperience addWorkExperience(WorkExperience workExperience, User user) {
        user.getWorkExperiences().add(workExperience);

        return workExperienceRepository.save(workExperience);
    }

    public WorkExperience getWorkExperienceById(Long id) {
        Optional<WorkExperience> optionalWorkExperience = workExperienceRepository.findById(id);
        return optionalWorkExperience.orElse(null);
    }

    public WorkExperience updateWorkExperience(Long id, WorkExperience workExperience) {
        if (workExperienceRepository.existsById(id)) {
            workExperience.setId(id); // Ensure the ID is set for update
            return workExperienceRepository.save(workExperience);
        }
        return null;
    }

    public void deleteWorkExperience(Long id) {
        workExperienceRepository.deleteById(id);
    }

    public boolean equalsWorkExperience(WorkExperience workExperience1, WorkExperience workExperience2){
        if (workExperience1.getTitle().trim().equalsIgnoreCase(workExperience2.getTitle().trim()) &&
            workExperience1.getCompanyName().trim().equalsIgnoreCase(workExperience2.getCompanyName().trim()) &&
            workExperience1.getEmploymentType().toString().trim().equalsIgnoreCase(workExperience2.getEmploymentType().toString().trim()) &&
            workExperience1.getStartDate().equals(workExperience2.getStartDate()) &&
            workExperience1.getEndDate().equals(workExperience2.getEndDate()) &&
            workExperience1.getUser().getId() == workExperience2.getUser().getId()) {
            return true;
        }

        return false;
    }
}
