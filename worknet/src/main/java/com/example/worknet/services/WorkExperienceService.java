package com.example.worknet.services;

import com.example.worknet.entities.WorkExperience;

import java.util.List;

public interface WorkExperienceService {
    List<WorkExperience> getAllWorkExperiences();
    WorkExperience addWorkExperience(WorkExperience workExperience);
    WorkExperience getWorkExperienceById(Long id);
    WorkExperience updateWorkExperience(Long id, WorkExperience workExperience);
    void deleteWorkExperience(Long id);
}
