package com.example.worknet.services;

import com.example.worknet.entities.Education;
import com.example.worknet.entities.User;

import java.util.List;

public interface EducationService {
    List<Education> getAllEducations();
    Education addEducation(Education education, User user);
    Education getEducationById(Long id);
    Education updateEducation(Long id, Education education);
    void deleteEducation(Long id);
}
