package com.example.worknet.dto;

import java.util.List;

public class SmallUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private List<CustomFileDTO> files;
    private List<WorkExperienceDTO> workExperiences;
    private List<SkillDTO> skills;
    private List<EducationDTO> educations;
    

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

    public String getProfilePicture(){
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    public List<CustomFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<CustomFileDTO> files) {
        this.files = files;
    }

    public List<WorkExperienceDTO> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperienceDTO> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public List<EducationDTO> getEducations() {
        return educations;
    }

    public void setEducations(List<EducationDTO> educations) {
        this.educations = educations;
    }
}
