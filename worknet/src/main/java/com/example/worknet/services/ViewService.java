package com.example.worknet.services;

import java.util.List;

import com.example.worknet.entities.View;

public interface ViewService {
    View getViewById(Long id);
    List<View> getAllViews();
    View addView(View view);
    View updateView(Long id,View view);
    void deleteView(Long id);
}
