package com.example.worknet.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String jwtToken;
    private List<CustomFileDTO> files;
    private List<SmallUserDTO> connections;
    private List<SmallJobDTO> appliedJobs;
    private List<UserLikeDTO> likes;
    private List<SmallPostDTO> posts;
    private List<MessageDTO> messages;
    private List<SkillDTO> skills;
    private List<NotificationDTO> notifications;


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

    public List<CustomFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<CustomFileDTO> files) {
        this.files = files;
    }

    public List<SmallUserDTO> getConnections() {
        return connections;
    }

    public void setConnections(List<SmallUserDTO> connections) {
        this.connections = connections;
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

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public List<SmallPostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<SmallPostDTO> posts) {
        this.posts = posts;
    }
}