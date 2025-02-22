package com.example.worknet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class PostDTO {

    private Long id;
    private String description;
    private EnlargedUserDTO user;
    private List<CustomFileDTO> customFiles;
    private List<PostLikeDTO> likes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate postCreationDate;
    private List<NotificationDTO> notifications;
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

    public EnlargedUserDTO getUser() {
        return user;
    }

    public void setUser(EnlargedUserDTO user) {
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

    public LocalDate getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(LocalDate postCreationDate) {
        this.postCreationDate = postCreationDate;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
