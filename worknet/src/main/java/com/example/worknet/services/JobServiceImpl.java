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

//        if (job.getSkills() == null) {
//            List<Skill> skills = new ArrayList<>();
//            skills.add(skill);
//            job.setSkills(skills);
//        }else{
//            job.getSkills().add(skill);
//        }

        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job job, List<String> skillNames) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            // Delete all skills first to satisfy constraint
            // and save new ones (or same ones in case of no updating on skills)

//            for (Skill skill : job.getSkills()) {
//                skill.getJob().remove;
//            }
            Job existingJob = jobOptional.get();

            // Remove and delete existing skills
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

//            List<Skill> jobSkillsList = new ArrayList<>(job.getSkills());
//            for (Skill s: jobSkillsList) {
//                s.setJob(null);
//                skillRepository.save(s);
//            }
//
//            job.getSkills().removeAll(jobSkillsList);
//
//            jobRepository.save(job);
//
//            skillRepository.deleteAll(jobSkillsList);
//
//            List<Skill> skillsList = new ArrayList<>();
//
//            for (String skillName : skillNames) {
//                Skill skill = new Skill();
//                skill.setName(skillName);
//                skill.setJob(job);
//                skill.setIsPublic(true);
//                skillsList.add(skill);
//            }
//
//            Job existingJob = jobOptional.get();
//
//            existingJob.setSkills(skillsList);
//
//            StrictModelMapper modelMapper = new StrictModelMapper();
//
//            modelMapper.map(job, existingJob);
//
//            return jobRepository.save(existingJob);
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
