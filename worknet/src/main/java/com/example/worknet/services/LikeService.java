package com.example.worknet.services;

import com.example.worknet.entities.Like;

import java.util.List;

public interface LikeService {
    List<Like> getAllLikes();
    Like addLike(Like like);
    Like getLikeById(Long id);
    Like updateLike(Long id, Like like);
    void deleteLike(Long id);
}
