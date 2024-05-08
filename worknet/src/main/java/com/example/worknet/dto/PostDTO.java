package com.example.worknet.dto;

import java.util.List;

public class PostDTO {

    private Long id;
    private String description;
    private UserDTO user;
    private List<CustomFileDTO> customFiles;

    public PostDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<CustomFileDTO> getCustomFiles() {
        return customFiles;
    }

    public void setCustomFiles(List<CustomFileDTO> customFiles) {
        this.customFiles = customFiles;
    }
}
