package com.example.worknet.services;


import com.example.worknet.entities.Message;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.MessageRepository;
import com.example.worknet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public Message getMessageById(Long id) {
        Optional<Message> message = messageRepository.findById(id);

        return message.orElse(null);
    }

    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public Message addMessage(Message message){
        return messageRepository.save(message);
    }

    public Message updateMessage(Long id, Message message) {
        Optional<Message> messageOptional = messageRepository.findById(id);
        if (messageOptional.isPresent()) {
            Message existingMessage = messageOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(message, existingMessage);

            return messageRepository.save(existingMessage);
        }

        return null;
    }

    public void deleteMessage(Long id){
        messageRepository.deleteById(id);
    }
}
