package com.example.worknet.services;

import com.example.worknet.entities.Post;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    public Post getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);

        return post.orElse(null);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post addPost(Post post){
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post post) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post existingPost = postOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(post, existingPost);

            return postRepository.save(existingPost);
        }

        return null;
    }

    public void deletePost(Long id){
        postRepository.deleteById(id);
    }

    public List<Post> searchPostByDescription(String description){
        return postRepository.findByDescriptionContainingIgnoreCase(description);
    }
}
