package com.example.worknet.services;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.Skill;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job getJobById(Long id) {
        Optional<Job> job = jobRepository.findById(id);

        return job.orElse(null);
    }

    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    public Job addJob(Job job){
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job job) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job existingJob = jobOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(job, existingJob);

            return jobRepository.save(existingJob);
        }

        return null;
    }

    public void deleteJob(Long id){
        jobRepository.deleteById(id);
    }

    
    public void addSkill(Long id, String skillName) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        
        Skill skill = new Skill();
        skill.setName(skillName);
        skill.setJob(job);

        job.getSkills().add(skill);

        jobRepository.save(job);
        
    }
}
