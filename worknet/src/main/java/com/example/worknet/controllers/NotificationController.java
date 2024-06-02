package com.example.worknet.controllers;


import com.example.worknet.dto.NotificationDTO;
import com.example.worknet.entities.Notification;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();

        List<NotificationDTO> notificationDTOList =  notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .toList();

        return ResponseEntity.ok(notificationDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);

        NotificationDTO notificationDTO =  modelMapper.map(notification, NotificationDTO.class);
        if (notificationDTO != null){
            return ResponseEntity.ok(notificationDTO);
        }else{
            String errorMessage = "Notification with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            Notification notification = modelMapper.map(notificationDTO, Notification.class);

            notificationService.addNotification(notification);

            return ResponseEntity.status(HttpStatus.CREATED).body("Notification added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add Notification: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/sendNotification")
    public ResponseEntity<?> addView(@RequestParam Long userId,
                                     @RequestParam Long postId) {
        try {
            notificationService.sendNotification(userId, postId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Sent notification successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to send notification: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        try {

            Notification existingNotification = notificationService.getNotificationById(id);
            if (existingNotification == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Notification with ID " + id + " does not exist.");
            }

            modelMapper.map(notificationDTO, existingNotification);

            notificationService.updateNotification(id, existingNotification);

            return ResponseEntity.ok("Notification updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update notification with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {

            Notification existingNotification = notificationService.getNotificationById(id);
            if (existingNotification == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Notification with ID " + id + " does not exist.");
            }

            notificationService.deleteNotification(id);

            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete notification: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
