package com.example.worknet.services;

import com.example.worknet.entities.Message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages();
    Message addMessage(Message message);
    Message getMessageById(Long id);
    Message updateMessage(Long id, Message message);
    void deleteMessage(Long id);
}
