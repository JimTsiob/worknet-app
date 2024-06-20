package com.syrtsiob.worknet.services;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CustomFileService {
    @Multipart
    @POST("customFiles/uploadImage")
    Call<String> uploadImage(@Part MultipartBody.Part file, @Query("userId") Long userId);

    @Multipart
    @POST("customFiles/uploadPostFiles")
    Call<String> uploadPostFiles(@Part List<MultipartBody.Part> files, @Query("userId") Long userId, @Query("postId") Long postId);
}
