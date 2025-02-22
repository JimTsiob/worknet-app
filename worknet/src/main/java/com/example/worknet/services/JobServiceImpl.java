package com.example.worknet.services;

import com.example.worknet.entities.Job;
import com.example.worknet.entities.Skill;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.JobRepository;
import com.example.worknet.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SkillRepository skillRepository;

    public Job getJobById(Long id) {
        Optional<Job> job = jobRepository.findById(id);

        return job.orElse(null);
    }

    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    public Job addJob(Job job, List<String> skills) {
        List<Skill> skillsList = new ArrayList<>();

        for (String skillName : skills) {
            Skill skill = new Skill();
            skill.setName(skillName);
            skill.setJob(job);
            skill.setIsPublic(true);
            skillsList.add(skill);
        }

        job.setSkills(skillsList);

        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job job, List<String> skillNames) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {

            Job existingJob = jobOptional.get();

            // Remove and delete existing skills to satisfy constraint
            List<Skill> existingSkills = new ArrayList<>(existingJob.getSkills());

            for (Skill skill : existingJob.getSkills()) {
                skill.setJob(null);
                skillRepository.save(skill);
            }

            existingJob.getSkills().clear();
            jobRepository.save(existingJob); // Save the job after clearing skills

            skillRepository.deleteAll(existingSkills); // Delete all removed skills

            // Create new skills and associate them with the job
            List<Skill> newSkills = new ArrayList<>();
            for (String skillName : skillNames) {
                Skill newSkill = new Skill();
                newSkill.setName(skillName);
                newSkill.setJob(existingJob);
                newSkill.setIsPublic(true);
                newSkills.add(newSkill);
            }

            // Set the new skills to the job
            existingJob.setSkills(newSkills);

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(job, existingJob);

            // Save the job with the new skills
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

    public boolean equalsJob(Job job1, Job job2){
        if (job1.getJobTitle().trim().equalsIgnoreCase(job2.getJobTitle().trim()) &&
            job1.getCompany().trim().equalsIgnoreCase(job2.getCompany().trim()) &&
            job1.getEmploymentType().toString().trim().equalsIgnoreCase(job2.getEmploymentType().toString().trim()) &&
            job1.getJobLocation().trim().equalsIgnoreCase(job2.getJobLocation().trim()) &&
            job1.getWorkplaceType().toString().trim().equalsIgnoreCase(job2.getWorkplaceType().toString().trim()) &&
            job1.getJobPoster().getId() == job2.getJobPoster().getId()) {
            return true;
        }

        return false;
    }
}
