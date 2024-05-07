package com.example.worknet.services;

import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElse(null);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(user, existingUser);

            return userRepository.save(existingUser);
        }

        return null;
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
