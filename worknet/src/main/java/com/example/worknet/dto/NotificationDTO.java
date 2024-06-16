package com.example.worknet.dto;

import com.example.worknet.enums.NotificationType;

public class NotificationDTO {

    private Long id;
    private String text;
    private SmallUserDTO user;
    private SmallPostDTO post;
    private NotificationType notificationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SmallUserDTO getUser() {
        return user;
    }

    public void setUser(SmallUserDTO user) {
        this.user = user;
    }

    public SmallPostDTO getPost() {
        return post;
    }

    public void setPost(SmallPostDTO post) {
        this.post = post;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}
