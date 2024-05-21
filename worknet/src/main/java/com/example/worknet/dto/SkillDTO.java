package com.example.worknet.dto;

public class SkillDTO {

    private Long id;
    private String name;
    private SmallUserDTO user;
    private SmallJobDTO job;

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
}

