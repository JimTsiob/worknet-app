package com.example.worknet.controllers;

import com.example.worknet.dto.JobDTO;
import com.example.worknet.entities.Job;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        List<JobDTO> jobDTOList =  jobs.stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .toList();

        return ResponseEntity.ok(jobDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);

        JobDTO jobDTO =  modelMapper.map(job, JobDTO.class);
        if (jobDTO != null){
            return ResponseEntity.ok(jobDTO);
        }else{
            String errorMessage = "Job with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addJob(@RequestBody JobDTO jobDTO) {
        try {
            Job job = modelMapper.map(jobDTO, Job.class);

            jobService.addJob(job);

            return ResponseEntity.status(HttpStatus.CREATED).body("Job added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add job: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        try {

            Job existingJob = jobService.getJobById(id);
            if (existingJob == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Job with ID " + id + " does not exist.");
            }

            modelMapper.map(jobDTO, existingJob);

            jobService.updateJob(id, existingJob);

            return ResponseEntity.ok("Job updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update job with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {

            Job existingJob = jobService.getJobById(id);
            if (existingJob == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Job with ID " + id + " does not exist.");
            }

            // remove job applications first to satisfy constraint.

            for (User user : existingJob.getInterestedUsers()) {
                user.getAppliedJobs().removeIf(job -> job.getId().equals(id));
            }

            existingJob.getInterestedUsers().clear();
            jobService.updateJob(id, existingJob);

            jobService.deleteJob(id);

            return ResponseEntity.ok("Job deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete job: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
