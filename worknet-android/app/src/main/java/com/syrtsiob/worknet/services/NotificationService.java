package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.NotificationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationService {

    @POST("notifications/")
    Call<String> addNotification(@Body NotificationDTO notificationDTO);
}
