package com.example.worknet.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String jwtToken;
    private String password;
    private String profilePicture;
    private List<CustomFileDTO> files;
    private List<EnlargedUserDTO> connections;
    private List<JobDTO> jobs;
    private List<SmallJobDTO> appliedJobs;
    private List<UserLikeDTO> likes;
    private List<SmallPostDTO> posts;
    private List<MessageDTO> sentMessages;
    private List<MessageDTO> receivedMessages;
    private List<SkillDTO> skills;
    private List<EducationDTO> educations;
    private List<NotificationDTO> sentNotifications;
    private List<NotificationDTO> receivedNotifications;
    private List<WorkExperienceDTO> workExperiences;


    public UserDTO() {}

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
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

    public List<EnlargedUserDTO> getConnections() {
        return connections;
    }

    public void setConnections(List<EnlargedUserDTO> connections) {
        this.connections = connections;
    }

    public List<JobDTO> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobDTO> jobs) {
        this.jobs = jobs;
    }

    public List<SmallJobDTO> getAppliedJobs() {
        return appliedJobs;
    }

    public void setAppliedJobs(List<SmallJobDTO> appliedJobs) {
        this.appliedJobs = appliedJobs;
    }

    public List<UserLikeDTO> getLikes() {
        return likes;
    }

    public void setLikes(List<UserLikeDTO> likes) {
        this.likes = likes;
    }

    public List<MessageDTO> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<MessageDTO> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public List<MessageDTO> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<MessageDTO> receivedMessages) {
        this.receivedMessages = receivedMessages;
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

    public List<NotificationDTO> getSentNotifications() {
        return sentNotifications;
    }

    public void setSentNotifications(List<NotificationDTO> sentNotifications) {
        this.sentNotifications = sentNotifications;
    }

    public List<NotificationDTO> getReceivedNotifications() {
        return receivedNotifications;
    }

    public void setReceivedNotifications(List<NotificationDTO> receivedNotifications) {
        this.receivedNotifications = receivedNotifications;
    }

    public List<SmallPostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<SmallPostDTO> posts) {
        this.posts = posts;
    }

    public List<WorkExperienceDTO> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperienceDTO> workExperiences) {
        this.workExperiences = workExperiences;
    }
}