package com.example.worknet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.worknet.entities.View;

@Repository
public interface ViewRepository extends JpaRepository<View, Long>{
    
}
