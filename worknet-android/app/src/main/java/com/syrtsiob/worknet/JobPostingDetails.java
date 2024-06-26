package com.syrtsiob.worknet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.NotificationService;
import com.syrtsiob.worknet.services.UserService;

import java.util.Objects;
import java.util.StringJoiner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class JobPostingDetails extends AppCompatActivity {

    static final String SERIALIZABLE = "serializable";

    static final Long USER_ID = 0L;

    TextView jobTitle, company, workplaceType, location, employmentType, description, skills;
    Button returnButton, applyButton;

    JobDTO jobDTO;

    Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting_details);

        jobDTO = getIntent().getSerializableExtra(SERIALIZABLE, JobDTO.class);
        userId = getIntent().getLongExtra(USER_ID.toString(), 0L);

        jobTitle = findViewById(R.id.jobTitle);
        company = findViewById(R.id.company);
        workplaceType = findViewById(R.id.workplaceType);
        location = findViewById(R.id.jobLocation);
        employmentType = findViewById(R.id.employmentType);
        description = findViewById(R.id.jobDescription);
        skills = findViewById(R.id.jobSkills);

        jobTitle.setText(jobDTO.getJobTitle());
        company.setText(jobDTO.getCompany());

        if (jobDTO.getEmploymentType().toString().equals("FULL_TIME")){
            employmentType.setText("Full time position");
        }else if (jobDTO.getEmploymentType().toString().equals("PART_TIME")){
            employmentType.setText("Part time position");
        }else{
            employmentType.setText("Contract position");
        }

        if (jobDTO.getWorkplaceType().toString().equals("ON_SITE")){
            workplaceType.setText("On site");
        }else if (jobDTO.getWorkplaceType().toString().equals("REMOTE")){
            workplaceType.setText("Remote");
        }else{
            workplaceType.setText("Hybrid");
        }

        location.setText(jobDTO.getJobLocation());
        description.setText(jobDTO.getDescription());

        // show each skill separated by a comma and space, apart from the last one
        StringJoiner skillset = new StringJoiner(", ");
        for (SkillDTO skill : jobDTO.getSkills()) {
            skillset.add(skill.getName());
        }

        skills.setText("Skills: " + skillset.toString());

        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(listener -> {
            finish();
        });

        applyButton = findViewById(R.id.applyButton);

        boolean userIsApplicant = false;

        for (EnlargedUserDTO applicant: jobDTO.getInterestedUsers()){
            if (Objects.equals(applicant.getId(), userId)){
                userIsApplicant = true;
            }
        }

        if (userIsApplicant){
            applyButton.setEnabled(false);
        }

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);
        NotificationService notificationService = retrofit.create(NotificationService.class);

        applyButton.setOnClickListener(listener -> {

            NotificationDTO notificationDTO = new NotificationDTO();
            NotificationType notificationType = NotificationType.valueOf("APPLY_TO_JOB_POST");
            notificationDTO.setNotificationType(notificationType);

            notificationDTO.setReceiver(jobDTO.getJobPoster());

            // Send notification to job poster
            userService.getUserById(userId).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        EnlargedUserDTO sender = new EnlargedUserDTO(); // taking this object type for ease.
                        sender.setId(response.body().getId());
                        sender.setEducations(response.body().getEducations());
                        sender.setEmail(response.body().getEmail());
                        sender.setWorkExperiences(response.body().getWorkExperiences());
                        sender.setSkills(response.body().getSkills());
                        sender.setFiles(response.body().getFiles());
                        sender.setProfilePicture(response.body().getProfilePicture());
                        sender.setLastName(response.body().getLastName());
                        sender.setFirstName(response.body().getFirstName());

                        String notificationText = response.body().getFirstName() + " " + response.body().getLastName() + " has applied to your "
                                + jobDTO.getJobTitle() + " job posting.";

                        notificationDTO.setText(notificationText);
                        notificationDTO.setSender(sender);

                        notificationService.addNotification(notificationDTO).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()){
                                    // do nothing
                                }else{
                                    Toast.makeText(JobPostingDetails.this, "Notification addition failed! Check the format.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(JobPostingDetails.this, "Notification addition failed! Server failure.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else{
                        Toast.makeText(JobPostingDetails.this, "User by id failed! Check the format", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("user by id fail: ", t.getLocalizedMessage());
                    Toast.makeText(JobPostingDetails.this, "User by id failed! Server failure.", Toast.LENGTH_LONG).show();
                }
            });


            // Apply to job


            userService.applyToJob(userId,jobDTO.getId()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(JobPostingDetails.this, "Applied to job successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(JobPostingDetails.this, "job application failed. Check the format", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("fail: ", t.getLocalizedMessage());
                    // Handle the error
                    Toast.makeText(JobPostingDetails.this, "job application failed! Server failure.", Toast.LENGTH_LONG).show();
                }
            });

            finish();
        });


        OnBackPressedCallback finishWhenBackPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, finishWhenBackPressed);

        AddPostingViewToDatabase();
    }

    private void AddPostingViewToDatabase() {
        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);
        userService.addView(userId,jobDTO.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JobPostingDetails.this, "Job viewed!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(JobPostingDetails.this, "job view failed. Check the format", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(JobPostingDetails.this, "jobs view failed! Server failure.", Toast.LENGTH_LONG).show();
            }
        });
    }
}