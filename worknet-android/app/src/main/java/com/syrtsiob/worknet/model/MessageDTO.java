package com.syrtsiob.worknet.model;

import java.io.Serializable;
import java.util.List;

public class MessageDTO implements Serializable {

    private Long id;
    private String text;
    private EnlargedUserDTO sender;
    private EnlargedUserDTO receiver;

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

    public EnlargedUserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(EnlargedUserDTO receiver) {
        this.receiver = receiver;
    }
}
