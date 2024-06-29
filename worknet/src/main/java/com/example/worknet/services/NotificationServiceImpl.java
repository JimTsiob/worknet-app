package com.example.worknet.services;


import com.example.worknet.entities.*;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.NotificationRepository;
import com.example.worknet.repositories.PostRepository;
import com.example.worknet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public Notification getNotificationById(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        return notification.orElse(null);
    }

    public List<Notification> getAllNotifications(){
        return notificationRepository.findAll();
    }

    public Notification addNotification(Notification notification){
        return notificationRepository.save(notification);
    }

    public Notification updateNotification(Long id, Notification notification) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        if (notificationOptional.isPresent()) {
            Notification existingNotification = notificationOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(notification, existingNotification);

            return notificationRepository.save(existingNotification);
        }

        return null;
    }

    public void deleteNotification(Long id){
        notificationRepository.deleteById(id);
    }
}
