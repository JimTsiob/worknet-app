package com.syrtsiob.worknet.model;

import com.syrtsiob.worknet.enums.NotificationType;

public class NotificationDTO {
    private Long id;
    private String text;
    private SmallPostDTO post;
    private EnlargedUserDTO sender;
    private SmallUserDTO receiver;

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

    public EnlargedUserDTO getSender() {
        return sender;
    }

    public void setSender(EnlargedUserDTO sender) {
        this.sender = sender;
    }

    public SmallUserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(SmallUserDTO receiver) {
        this.receiver = receiver;
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
