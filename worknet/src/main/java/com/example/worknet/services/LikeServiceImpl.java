package com.example.worknet.services;

import com.example.worknet.entities.Like;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService{

    @Autowired
    private LikeRepository likeRepository;

    public Like getLikeById(Long id) {
        Optional<Like> like = likeRepository.findById(id);

        return like.orElse(null);
    }

    public List<Like> getAllLikes(){
        return likeRepository.findAll();
    }

    public Like addLike(Like like){
        return likeRepository.save(like);
    }

    public Like updateLike(Long id, Like like) {
        Optional<Like> likeOptional = likeRepository.findById(id);
        if (likeOptional.isPresent()) {
            Like existingLike = likeOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(like, existingLike);

            return likeRepository.save(existingLike);
        }

        return null;
    }

    public void deleteLike(Long id){
        likeRepository.deleteById(id);
    }
}
