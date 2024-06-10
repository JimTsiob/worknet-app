package com.syrtsiob.worknet.services;

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
}
