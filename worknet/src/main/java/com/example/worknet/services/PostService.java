package com.example.worknet.services;

import com.example.worknet.entities.CustomFile;
import com.example.worknet.entities.Post;

import java.util.List;

public interface PostService {
    Post getPostById(Long id);
    List<Post> getAllPosts();
    Post addPost(Post post);
    Post updatePost(Long id, Post post);
    void deletePost(Long id);
}
