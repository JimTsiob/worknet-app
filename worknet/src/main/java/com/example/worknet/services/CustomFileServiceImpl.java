package com.example.worknet.services;

import com.example.worknet.entities.CustomFile;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.CustomFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomFileServiceImpl implements CustomFileService {

    @Autowired
    private CustomFileRepository customFileRepository;

    public CustomFile getCustomFileById(Long id) {
        Optional<CustomFile> customFile = customFileRepository.findById(id);

        return customFile.orElse(null);
    }

    public List<CustomFile> getAllCustomFiles(){
        return customFileRepository.findAll();
    }

    public CustomFile addCustomFile(CustomFile customFile){
        return customFileRepository.save(customFile);
    }

    public CustomFile updateCustomFile(Long id, CustomFile customFile) {
        Optional<CustomFile> customFileOptional = customFileRepository.findById(id);
        if (customFileOptional.isPresent()) {
            CustomFile existingCustomFile = customFileOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(customFile, existingCustomFile);

            return customFileRepository.save(existingCustomFile);
        }

        return null;
    }

    public void deleteCustomFile(Long id){
        customFileRepository.deleteById(id);
    }
}
