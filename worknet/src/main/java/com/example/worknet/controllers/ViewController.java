package com.example.worknet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.worknet.dto.ViewDTO;
import com.example.worknet.entities.View;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.ViewService;

@RestController
@RequestMapping("/views")
public class ViewController {

    @Autowired
    private ViewService viewService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllViews() {
        List<View> views = viewService.getAllViews();

        List<ViewDTO> viewDTOList =  views.stream()
                .map(view -> modelMapper.map(view, ViewDTO.class))
                .toList();

        return ResponseEntity.ok(viewDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getViewById(@PathVariable Long id) {
        View view = viewService.getViewById(id);

        ViewDTO viewDTO =  modelMapper.map(view, ViewDTO.class);
        if (viewDTO != null){
            return ResponseEntity.ok(viewDTO);
        }else{
            String errorMessage = "View with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addView(@RequestBody ViewDTO viewDTO) {
        try {
            View view = modelMapper.map(viewDTO, View.class);

            viewService.addView(view);

            return ResponseEntity.status(HttpStatus.CREATED).body("View added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add view: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateView(@PathVariable Long id, @RequestBody ViewDTO viewDTO) {
        try {

            View existingView = viewService.getViewById(id);
            if (existingView == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("View with ID " + id + " does not exist.");
            }

            modelMapper.map(viewDTO, existingView);

            viewService.updateView(id, existingView);

            return ResponseEntity.ok("View updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update view with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteView(@PathVariable Long id) {
        try {

            View existingView = viewService.getViewById(id);
            if (existingView == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("View with ID " + id + " does not exist.");
            }

            viewService.deleteView(id);

            return ResponseEntity.ok("View deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete view: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
