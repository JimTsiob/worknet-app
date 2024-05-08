package com.example.worknet.repositories;

import com.example.worknet.entities.CustomFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFileRepository extends JpaRepository<CustomFile, Long> {
}
