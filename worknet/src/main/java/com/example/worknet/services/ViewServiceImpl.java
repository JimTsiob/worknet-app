package com.example.worknet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.worknet.entities.View;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.ViewRepository;

@Service
public class ViewServiceImpl implements ViewService{

    @Autowired
    private ViewRepository viewRepository;
    
    public View getViewById(Long id) {
        Optional<View> view = viewRepository.findById(id);

        return view.orElse(null);
    }

    
    public List<View> getAllViews() {
        return viewRepository.findAll();        
    }

    
    public View addView(View view) {
        return viewRepository.save(view);
    }

    
    public View updateView(Long id, View view) {
        Optional<View> viewOptional = viewRepository.findById(id);
        if (viewOptional.isPresent()) {
            View existingView = viewOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(view, existingView);

            return viewRepository.save(existingView);
        }

        return null;
    }

    
    public void deleteView(Long id) {
        viewRepository.deleteById(id);
    }
    
}
