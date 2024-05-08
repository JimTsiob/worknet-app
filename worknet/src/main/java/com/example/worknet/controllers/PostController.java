package com.example.worknet.controllers;


import com.example.worknet.dto.PostDTO;
import com.example.worknet.entities.Post;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postService.getAllPosts();

        List<PostDTO> postDTOList =  posts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList();

        return ResponseEntity.ok(postDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);

        PostDTO postDTO =  modelMapper.map(post, PostDTO.class);
        if (postDTO != null){
            return ResponseEntity.ok(postDTO);
        }else{
            String errorMessage = "Post with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addPost(@RequestBody PostDTO postDTO) {
        try {
            Post post = modelMapper.map(postDTO, Post.class);

            postService.addPost(post);

            return ResponseEntity.status(HttpStatus.CREATED).body("Post added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add post: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        try {

            Post existingPost = postService.getPostById(id);
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Post with ID " + id + " does not exist.");
            }

            modelMapper.map(postDTO, existingPost);

            postService.updatePost(id, existingPost);

            return ResponseEntity.ok("Post updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update post with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {

            Post existingPost = postService.getPostById(id);
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Post with ID " + id + " does not exist.");
            }

            postService.deletePost(id);

            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete post: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
