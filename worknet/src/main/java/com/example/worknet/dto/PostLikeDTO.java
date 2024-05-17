package com.example.worknet.dto;

public class PostLikeDTO {
    private Long id;
    private SmallUserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmallUserDTO getUser() {
        return user;
    }

    public void setUser(SmallUserDTO user) {
        this.user = user;
    }
}
