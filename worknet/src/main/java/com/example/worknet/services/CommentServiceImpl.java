package com.example.worknet.services;


import com.example.worknet.entities.Comment;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment getCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        return comment.orElse(null);
    }

    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }

    public Comment addComment(Comment comment){
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Comment comment) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            Comment existingComment = commentOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(comment, existingComment);

            return commentRepository.save(existingComment);
        }

        return null;
    }

    public void deleteComment(Long id){
        commentRepository.deleteById(id);
    }
}
