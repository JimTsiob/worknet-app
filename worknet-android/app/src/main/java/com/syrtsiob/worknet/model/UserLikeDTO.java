package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class UserLikeDTO implements Serializable {

    private Long id;
    private SmallPostDTO post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmallPostDTO getPost() {
        return post;
    }

    public void setPost(SmallPostDTO post) {
        this.post = post;
    }
}
