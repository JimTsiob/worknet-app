package com.example.worknet.controllers;


import com.example.worknet.dto.LikeDTO;
import com.example.worknet.entities.Like;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllLikes() {
        List<Like> likes = likeService.getAllLikes();

        List<LikeDTO> likeDTOList =  likes.stream()
                .map(like -> modelMapper.map(like, LikeDTO.class))
                .toList();

        return ResponseEntity.ok(likeDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);

        LikeDTO likeDTO =  modelMapper.map(like, LikeDTO.class);
        if (likeDTO != null){
            return ResponseEntity.ok(likeDTO);
        }else{
            String errorMessage = "Like with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addLike(@RequestBody LikeDTO likeDTO) {
        try {
            Like like = modelMapper.map(likeDTO, Like.class);

            likeService.addLike(like);

            return ResponseEntity.status(HttpStatus.CREATED).body("Like added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add like: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLike(@PathVariable Long id, @RequestBody LikeDTO likeDTO) {
        try {

            Like existingLike = likeService.getLikeById(id);
            if (existingLike == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Like with ID " + id + " does not exist.");
            }

            modelMapper.map(likeDTO, existingLike);

            likeService.updateLike(id, existingLike);

            return ResponseEntity.ok("Like updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update like with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLike(@PathVariable Long id) {
        try {

            Like existingLike = likeService.getLikeById(id);
            if (existingLike == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Like with ID " + id + " does not exist.");
            }

            likeService.deleteLike(id);

            return ResponseEntity.ok("Like deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete like: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
