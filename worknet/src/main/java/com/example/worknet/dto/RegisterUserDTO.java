package com.example.worknet.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
