package com.example.worknet.repositories;

import com.example.worknet.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByDescriptionContainingIgnoreCase(String description);
}
