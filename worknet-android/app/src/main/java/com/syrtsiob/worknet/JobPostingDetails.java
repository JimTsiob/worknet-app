package com.syrtsiob.worknet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.syrtsiob.worknet.model.ApplicantDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.UserService;

import java.util.List;
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

        for (ApplicantDTO applicant: jobDTO.getInterestedUsers()){
            if (Objects.equals(applicant.getId(), userId)){
                userIsApplicant = true;
            }
        }

        if (userIsApplicant){
            applyButton.setEnabled(false);
        }

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        applyButton.setOnClickListener(listener -> {

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