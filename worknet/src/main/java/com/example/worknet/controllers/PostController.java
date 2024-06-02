package com.example.worknet.controllers;


import com.example.worknet.dto.PostDTO;
import com.example.worknet.entities.Post;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.PostService;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

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

    // return all posts from user who sees their front page and their connections.
    // sort them descendingly too to see the newest ones first.
    @GetMapping("/front-page")
    public ResponseEntity<?> getFrontPosts(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            String errorMessage = "User with ID " + userId + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        List<Post> userPosts = user.getPosts();

        List<PostDTO> postDTOList = new ArrayList<>(userPosts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList());

        for (User conUser : user.getConnections()) {
            for (Post post : conUser.getPosts()) {
                PostDTO postDTO = modelMapper.map(post, PostDTO.class);
                postDTOList.add(postDTO);
            }
        }

        // sort by descending order to get the newest posts to show first.
        postDTOList.sort((p1, p2) -> p2.getPostCreationDate().compareTo(p1.getPostCreationDate()));

        return ResponseEntity.ok(postDTOList);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam String description) {
        List<Post> posts = postService.searchPostByDescription(description);

        List<PostDTO> postDTOList =  posts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList();

        return ResponseEntity.ok(postDTOList);
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
