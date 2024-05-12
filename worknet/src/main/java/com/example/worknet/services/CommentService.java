package com.example.worknet.services;

import com.example.worknet.entities.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();
    Comment addComment(Comment comment);
    Comment getCommentById(Long id);
    Comment updateComment(Long id, Comment comment);
    void deleteComment(Long id);
}
