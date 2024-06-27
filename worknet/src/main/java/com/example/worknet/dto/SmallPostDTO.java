package com.example.worknet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class SmallPostDTO {
    private Long id;
    private String description;
    private EnlargedUserDTO user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate postCreationDate;

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

    public EnlargedUserDTO getUser() {
        return user;
    }

    public void setUser(EnlargedUserDTO user) {
        this.user = user;
    }

    public LocalDate getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(LocalDate postCreationDate) {
        this.postCreationDate = postCreationDate;
    }
}
