package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class CommentDTO implements Serializable {
    private Long id;
    private String text;
    private EnlargedUserDTO user;
    private SmallPostDTO post;

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

    public EnlargedUserDTO getUser() {
        return user;
    }

    public void setUser(EnlargedUserDTO user) {
        this.user = user;
    }

    public SmallPostDTO getPost() {
        return post;
    }

    public void setPost(SmallPostDTO post) {
        this.post = post;
    }
}
