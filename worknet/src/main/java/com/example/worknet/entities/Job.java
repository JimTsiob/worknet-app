package com.example.worknet.entities;


import com.example.worknet.enums.EmploymentType;
import com.example.worknet.enums.WorkplaceType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "company")
    private String company;

    @Enumerated(EnumType.STRING)
    private WorkplaceType workplaceType;

    @Column(name = "job_location")
    private String jobLocation;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @ManyToMany(mappedBy = "appliedJobs")
    private List<User> interestedUsers; // field for users who are interested in the job post

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<View> views;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User jobPoster; // field for user who posted the job

    public Job() {
    }

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

    public User getJobPoster() {
        return jobPoster;
    }

    public void setJobPoster(User jobPoster) {
        this.jobPoster = jobPoster;
    }

    public List<User> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<User> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }
}
