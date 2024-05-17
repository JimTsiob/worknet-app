package com.example.worknet.dto;

import java.util.List;

public class MessageDTO {
    private Long id;
    private String text;
    private List<SmallUserDTO> users;

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

    public List<SmallUserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<SmallUserDTO> users) {
        this.users = users;
    }
}
