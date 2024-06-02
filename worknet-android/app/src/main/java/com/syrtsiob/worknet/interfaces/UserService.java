package com.syrtsiob.worknet.interfaces;

import com.syrtsiob.worknet.model.LoginUserDTO;
import com.syrtsiob.worknet.model.RegisterUserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("users/login")
    Call<String> loginUser(@Body LoginUserDTO loginUserDTO);

    @POST("users/register")
    Call<String> registerUser(@Body RegisterUserDTO registerUserDTO);
}
