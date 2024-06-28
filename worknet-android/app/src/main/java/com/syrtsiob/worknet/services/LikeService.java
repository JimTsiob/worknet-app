package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.LikeDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LikeService {

    @POST("likes/")
    Call<String> addLike(@Body LikeDTO likeDTO);
}
