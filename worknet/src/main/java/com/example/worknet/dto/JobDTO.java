package com.example.worknet.dto;

import com.example.worknet.entities.Skill;
import com.example.worknet.enums.EmploymentType;
import com.example.worknet.enums.WorkplaceType;

import java.util.List;

public class JobDTO {

    private Long id;
    private String jobTitle;
    private String company;
    private WorkplaceType workplaceType;
    private String jobLocation;
    private EmploymentType employmentType;
    private SmallUserDTO jobPoster;
    private List<SmallUserDTO> interestedUsers;
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

    public SmallUserDTO getJobPoster() {
        return jobPoster;
    }

    public void setJobPoster(SmallUserDTO jobPoster) {
        this.jobPoster = jobPoster;
    }

    public List<SmallUserDTO> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<SmallUserDTO> interestedUsers) {
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
}