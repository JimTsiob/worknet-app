package com.syrtsiob.worknet.model;

import java.util.List;

public class ConnectionDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<WorkExperienceDTO> workExperiences;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<WorkExperienceDTO> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperienceDTO> workExperiences) {
        this.workExperiences = workExperiences;
    }
}
