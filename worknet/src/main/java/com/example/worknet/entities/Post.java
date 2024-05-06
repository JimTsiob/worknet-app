package com.example.worknet.entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "post")
    private List<CustomFile> customFiles;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public Post() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CustomFile> getCustomFiles() {
        return customFiles;
    }

    public void setCustomFiles(List<CustomFile> customFiles) {
        this.customFiles = customFiles;
    }
}
