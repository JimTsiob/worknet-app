package com.example.worknet.dto;

import com.example.worknet.enums.EmploymentType;
import com.example.worknet.enums.WorkplaceType;

import java.util.List;
import java.util.Objects;

public class JobDTO {

    private Long id;
    private String jobTitle;
    private String company;
    private WorkplaceType workplaceType;
    private String jobLocation;
    private EmploymentType employmentType;
    private String description;
    private SmallUserDTO jobPoster;
    private List<EnlargedUserDTO> interestedUsers;
    private List<ViewDTO> views;
    private List<SkillDTO> skills;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public WorkplaceType getWorkplaceType() {
        return workplaceType;
    }

    public void setWorkplaceType(WorkplaceType workplaceType) {
        this.workplaceType = workplaceType;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SmallUserDTO getJobPoster() {
        return jobPoster;
    }

    public void setJobPoster(SmallUserDTO jobPoster) {
        this.jobPoster = jobPoster;
    }

    public List<EnlargedUserDTO> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<EnlargedUserDTO> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }

    public List<ViewDTO> getViews() {
        return views;
    }

    public void setViews(List<ViewDTO> views) {
        this.views = views;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDTO myData = (JobDTO) o;
        return jobTitle.equals(myData.jobTitle) && Objects.equals(id, myData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, id);
    }
}