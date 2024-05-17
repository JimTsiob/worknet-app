package com.example.worknet.services;

import com.example.worknet.entities.Job;

import java.util.List;

public interface JobService {
    Job getJobById(Long id);
    List<Job> getAllJobs();
    Job addJob(Job job);
    Job updateJob(Long id,Job job);
    void deleteJob(Long id);
}
