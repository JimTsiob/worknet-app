package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.PostDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PostService {

    @POST("posts/")
    Call<String> addPost(@Body PostDTO postDTO);

    @GET("posts/")
    Call<List<PostDTO>> getAllPosts();
}
