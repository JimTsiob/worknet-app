package com.syrtsiob.worknet.interfaces;

import com.syrtsiob.worknet.model.LoginUserDTO;
import com.syrtsiob.worknet.model.RegisterUserDTO;
import com.syrtsiob.worknet.model.UserDTO;

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
}
