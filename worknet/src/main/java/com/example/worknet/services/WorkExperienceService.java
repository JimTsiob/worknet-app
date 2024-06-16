package com.example.worknet.services;

import com.example.worknet.entities.User;
import com.example.worknet.entities.WorkExperience;

import java.util.List;

public interface WorkExperienceService {
    List<WorkExperience> getAllWorkExperiences();
    WorkExperience addWorkExperience(WorkExperience workExperience, User user);
    WorkExperience getWorkExperienceById(Long id);
    WorkExperience updateWorkExperience(Long id, WorkExperience workExperience);
    void deleteWorkExperience(Long id);
    boolean equalsWorkExperience(WorkExperience workExperience1, WorkExperience workExperience2);
}
