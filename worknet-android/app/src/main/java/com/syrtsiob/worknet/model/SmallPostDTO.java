package com.syrtsiob.worknet.model;

public class SmallPostDTO {

    private Long id;
    private String description;
    private EnlargedUserDTO user;
    private String postCreationDate;

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

    public String getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(String postCreationDate) {
        this.postCreationDate = postCreationDate;
    }
}
