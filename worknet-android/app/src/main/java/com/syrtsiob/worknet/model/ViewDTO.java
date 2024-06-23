package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class ViewDTO implements Serializable {

    private Long id;
    private SmallUserDTO user;
    private SmallJobDTO job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmallUserDTO getUser() {
        return user;
    }

    public void setUser(SmallUserDTO user){
        this.user = user;
    }

    public SmallJobDTO getJob() {
        return job;
    }

    public void setJob(SmallJobDTO job){
        this.job = job;
    }
}
