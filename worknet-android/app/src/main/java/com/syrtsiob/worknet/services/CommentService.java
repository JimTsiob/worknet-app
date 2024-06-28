package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.CommentDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommentService {

    @POST("comments/")
    Call<String> addComment(@Body CommentDTO commentDTO);
}
