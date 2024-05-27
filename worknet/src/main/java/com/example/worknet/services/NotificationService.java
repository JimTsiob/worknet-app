package com.example.worknet.services;


import com.example.worknet.entities.Notification;
import com.example.worknet.entities.Post;

import java.util.List;

public interface NotificationService {
    Notification getNotificationById(Long id);
    List<Notification> getAllNotifications();
    Notification addNotification(Notification notification);
    Notification updateNotification(Long id, Notification notification);
    void deleteNotification(Long id);
    void sendNotification(Long userId, Long postId);
}
