package com.example.worknet.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<CustomFileDTO> files;
    private List<SmallUserDTO> connections;
    private List<SmallJobDTO> appliedJobs;


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
}