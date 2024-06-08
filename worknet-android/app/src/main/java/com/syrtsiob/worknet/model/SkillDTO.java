package com.syrtsiob.worknet.model;

import java.io.Serializable;

public class SkillDTO implements Serializable {

    private Long id;
    private String name;
    private SmallUserDTO user;
    private SmallJobDTO job;

    private boolean isPublic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SmallUserDTO getUser() {
        return user;
    }

    public void setUser(SmallUserDTO user) {
        this.user = user;
    }

    public SmallJobDTO getJob() {
        return job;
    }

    public void setJob(SmallJobDTO job) {
        this.job = job;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
