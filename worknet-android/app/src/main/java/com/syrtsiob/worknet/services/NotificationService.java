package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.NotificationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationService {

    @POST("notifications/")
    Call<String> addNotification(@Body NotificationDTO notificationDTO);

    @DELETE("notifications/{id}")
    Call<String> deleteNotification(@Path("id") Long id);
}
