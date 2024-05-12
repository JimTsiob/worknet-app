package com.example.worknet.controllers;

import com.example.worknet.dto.MessageDTO;
import com.example.worknet.entities.Message;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();

        List<MessageDTO> messageDTOList =  messages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .toList();

        return ResponseEntity.ok(messageDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);

        MessageDTO messageDTO =  modelMapper.map(message, MessageDTO.class);
        if (messageDTO != null){
            return ResponseEntity.ok(messageDTO);
        }else{
            String errorMessage = "Message with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addMessage(@RequestBody MessageDTO messageDTO) {
        try {
            Message message = modelMapper.map(messageDTO, Message.class);

            messageService.addMessage(message);

            return ResponseEntity.status(HttpStatus.CREATED).body("Message added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @RequestBody MessageDTO messageDTO) {
        try {

            Message existingMessage = messageService.getMessageById(id);
            if (existingMessage == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Message with ID " + id + " does not exist.");
            }

            modelMapper.map(messageDTO, existingMessage);

            messageService.updateMessage(id, existingMessage);

            return ResponseEntity.ok("Message updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update message with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        try {

            Message existingMessage = messageService.getMessageById(id);
            if (existingMessage == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Message with ID " + id + " does not exist.");
            }

            messageService.deleteMessage(id);

            return ResponseEntity.ok("Message deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
