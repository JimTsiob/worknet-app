package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class LikeDTO implements Serializable {

    private Long id;
    private SmallUserDTO user;
    private SmallPostDTO post;

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

    public SmallPostDTO getPost() {
        return post;
    }

    public void setPost(SmallPostDTO post) {
        this.post = post;
    }
}
