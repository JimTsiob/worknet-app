package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.JobDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JobService {
    @POST("jobs/")
//    Call<String> addJob(@Body JobDTO jobDTO, @Query("email") String email);
    Call<String> addJob(@Body JobDTO jobDTO, @Query("skillNames") List<String> skillNames);

    @PUT("jobs/{id}")
    Call<String> updateJob(@Path("id") Long jobId, @Body JobDTO jobDTO, @Query("skillNames") List<String> skillNames);

    @DELETE("jobs/{id}")
    Call<String> deleteJob(@Path("id") Long jobId);
}
