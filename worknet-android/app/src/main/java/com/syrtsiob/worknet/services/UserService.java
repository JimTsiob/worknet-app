package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.LoginUserDTO;
import com.syrtsiob.worknet.model.RegisterUserDTO;
import com.syrtsiob.worknet.model.SmallJobDTO;
import com.syrtsiob.worknet.model.UserDTO;

import java.util.List;

import kotlinx.coroutines.Job;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("users/login")
    Call<String> loginUser(@Body LoginUserDTO loginUserDTO);

    @POST("users/register")
    Call<String> registerUser(@Body RegisterUserDTO registerUserDTO);

    @GET("users/email")
    Call<UserDTO> getUserByEmail(@Query("email") String email);

    @PUT("users/{id}")
    Call<String> updateUser(@Path("id") Long userId, @Body UserDTO userDTO);

    @GET("users/logout")
    Call<String> logoutUser(@Query("email") String email);

    @GET("users/recommendation")
    Call<List<JobDTO>> recommendJobs(@Query("userId") Long userId);

    @POST("users/addView")
    Call<String> addView(@Query("userId") Long userId, @Query("jobId") Long jobId);

    @POST("users/applyToJob")
    Call<String> applyToJob(@Query("userId") Long userId, @Query("jobId") Long jobId);

    @GET("users/{id}")
    Call<UserDTO> getUserById(@Path("id") Long userId);

    @GET("users/search")
    Call<List<UserDTO>> searchUser(@Query("name") String name);

    @GET("users/addConnection")
    Call<String> addConnection(@Query("userId") Long userId, @Query("connectionId") Long connectionId);
}
