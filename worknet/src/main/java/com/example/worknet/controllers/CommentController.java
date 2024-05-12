package com.example.worknet.controllers;

import com.example.worknet.dto.CommentDTO;
import com.example.worknet.entities.Comment;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllComments() {
        List<Comment> comments = commentService.getAllComments();

        List<CommentDTO> commentDTOList =  comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .toList();

        return ResponseEntity.ok(commentDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);

        CommentDTO commentDTO =  modelMapper.map(comment, CommentDTO.class);
        if (commentDTO != null){
            return ResponseEntity.ok(commentDTO);
        }else{
            String errorMessage = "Comment with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO) {
        try {
            Comment comment = modelMapper.map(commentDTO, Comment.class);

            commentService.addComment(comment);

            return ResponseEntity.status(HttpStatus.CREATED).body("Comment added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add comment: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO) {
        try {

            Comment existingComment = commentService.getCommentById(id);
            if (existingComment == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Comment with ID " + id + " does not exist.");
            }

            modelMapper.map(commentDTO, existingComment);

            commentService.updateComment(id, existingComment);

            return ResponseEntity.ok("Comment updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update comment with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {

            Comment existingComment = commentService.getCommentById(id);
            if (existingComment == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Comment with ID " + id + " does not exist.");
            }

            commentService.deleteComment(id);

            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete comment: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
