package com.example.worknet.services;

import com.example.worknet.entities.Job;

import java.util.List;

public interface JobService {
    Job getJobById(Long id);
    List<Job> getAllJobs();
    Job addJob(Job job, List<String> skillNames);
    Job updateJob(Long id,Job job, List<String> skillNames);
    void deleteJob(Long id);
    void addSkill(Long id, String skillName);
    boolean equalsJob(Job job1, Job job2);
}
