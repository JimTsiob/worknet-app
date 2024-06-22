package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.MessageDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MessageService {

    @GET("messages/")
    Call<List<MessageDTO>> getAllMessages();

    @POST("messages/")
    Call<String> addMessage(@Body MessageDTO messageDTO);
}
