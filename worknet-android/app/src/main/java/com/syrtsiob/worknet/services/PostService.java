package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.PostDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.SmallPostDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {

    @POST("posts/")
    Call<String> addPost(@Body PostDTO postDTO);

    @GET("posts/")
    Call<List<PostDTO>> getAllPosts();

    @GET("posts/front-page")
    Call<List<SmallPostDTO>> getFrontPosts(@Query("userId") Long userId);

    @GET("posts/{id}")
    Call<PostDTO> getPostById(@Path("id") Long postId);

    @GET("posts/search")
    Call<List<SmallPostDTO>> searchPosts(@Query("description") String description);
}
