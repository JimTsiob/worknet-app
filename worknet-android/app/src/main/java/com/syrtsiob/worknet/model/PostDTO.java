package com.syrtsiob.worknet.model;

import java.io.Serializable;
import java.util.List;

public class PostDTO implements Serializable {

    private Long id;
    private String description;
    private SmallUserDTO user;
    private List<CustomFileDTO> customFiles;
    private List<PostLikeDTO> likes;
    private List<CommentDTO> comments;

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

    public SmallUserDTO getUser() {
        return user;
    }

    public void setUser(SmallUserDTO user) {
        this.user = user;
    }

    public List<CustomFileDTO> getCustomFiles() {
        return customFiles;
    }

    public void setCustomFiles(List<CustomFileDTO> customFiles) {
        this.customFiles = customFiles;
    }

    public List<PostLikeDTO> getLikes() {
        return likes;
    }

    public void setLikes(List<PostLikeDTO> likes) {
        this.likes = likes;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
