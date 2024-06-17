package com.syrtsiob.worknet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.EmploymentType;
import com.syrtsiob.worknet.enums.WorkplaceType;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.JobService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditJobPost extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    static final Long JOB_POST_ID = 0L;

    String activityMode;

    Long jobPostId;

    TextView activityTitle;

    EditText jobTitle, company, location, description, skills;
    Spinner workplaceType, employmentType;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_job_posting);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);
        jobPostId = getIntent().getLongExtra(JOB_POST_ID.toString(), 0L);

        activityTitle = findViewById(R.id.activityTitle);
        if (activityMode.equals(ADD_MODE))
            activityTitle.setText(R.string.create_job_posting);
        else if (activityMode.equals(EDIT_MODE)) {
            activityTitle.setText(R.string.edit_job_posting);
            JobDTO data = getIntent().getSerializableExtra(SERIALIZABLE, JobDTO.class);
            if (data != null)
                PopulateInputs(data);
        }

        jobTitle = findViewById(R.id.jobTitleInput);
        company = findViewById(R.id.companyInput);
        location = findViewById(R.id.locationInput);
        description = findViewById(R.id.descriptionInput);
        skills = findViewById(R.id.jobSkillInput);

        JobDTO jobDTO = new JobDTO();

        workplaceType = findViewById(R.id.workplaceTypeInput);
        String[] workplaceItems = WorkplaceType.getWorkplaceTypes();
        ArrayAdapter<String> workplaceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, workplaceItems);
        workplaceType.setAdapter(workplaceAdapter);

        employmentType = findViewById(R.id.employmentTypeInput);
        String[] employmentItems = EmploymentType.getEmploymentTypes();
        ArrayAdapter<String> employmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, employmentItems);
        employmentType.setAdapter(employmentAdapter);

        // This ensures that the dropdowns will have the correct input for the DB and for update.
        employmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Convert the selected item back to the corresponding EmploymentType enum
                EmploymentType selectedEmploymentType = EmploymentType.valueOf(selectedItem);

                jobDTO.setEmploymentType(selectedEmploymentType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Convert the selected item back to the corresponding EmploymentType enum
                EmploymentType selectedEmploymentType = EmploymentType.valueOf("FULL_TIME");

                jobDTO.setEmploymentType(selectedEmploymentType);
            }
        });

        workplaceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Convert the selected item back to the corresponding WorkplaceType enum
                WorkplaceType selectedWorkplaceType = WorkplaceType.valueOf(selectedItem);

                jobDTO.setWorkplaceType(selectedWorkplaceType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Convert the selected item back to the corresponding WorkplaceType enum
                WorkplaceType selectedWorkplaceType = WorkplaceType.valueOf("ON_SITE");

                jobDTO.setWorkplaceType(selectedWorkplaceType);
            }
        });

        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);

        cancelButton.setOnClickListener(listener -> {
            finish();
        });

        submitButton.setOnClickListener(listener -> {
            // no empty fields allowed
            if (isEmptyField())
                return;

            UserDtoResultLiveData.getInstance().observe(this, userDTO -> {
                Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
                JobService jobService = retrofit.create(JobService.class);

                SmallUserDTO smallUserDTO = new SmallUserDTO();
                smallUserDTO.setFirstName(userDTO.getFirstName());
                smallUserDTO.setLastName(userDTO.getLastName());
                smallUserDTO.setId(userDTO.getId());

                jobDTO.setDescription(description.getText().toString());
                jobDTO.setJobTitle(jobTitle.getText().toString());
                jobDTO.setJobLocation(location.getText().toString());
                jobDTO.setJobPoster(smallUserDTO);
                jobDTO.setCompany(company.getText().toString());

                if (activityMode.equals(ADD_MODE)){
                    List<String> skillList = new ArrayList<>(Arrays.asList(skills.getText().toString().split("\\s*,\\s*")));
                    jobService.addJob(jobDTO, skillList).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditJobPost.this, "added job post successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddEditJobPost.this, "job post addition failed. Check the format.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("fail: ", t.getLocalizedMessage());
                            // Handle the error
                            Toast.makeText(AddEditJobPost.this, "job post addition failed. Server failure.", Toast.LENGTH_LONG).show();
                        }
                    });

                }else if (activityMode.equals(EDIT_MODE)){
                    List<String> skillList = new ArrayList<>(Arrays.asList(skills.getText().toString().split("\\s*,\\s*")));
                    List<SkillDTO> emptySkillList = new ArrayList<>();
                    jobDTO.setSkills(emptySkillList);
                    jobService.updateJob(jobPostId, jobDTO, skillList).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditJobPost.this, "updated job post successfully.", Toast.LENGTH_LONG).show();

                                // Replace old job post with new one in the userDTO list.
                                ListIterator<JobDTO> iterator = userDTO.getJobs().listIterator();
                                while (iterator.hasNext()) {
                                    JobDTO next = iterator.next();
                                    if (next.getId() == jobPostId) {
                                        //Replace element
                                        jobDTO.setId(jobPostId);
                                        iterator.set(jobDTO);
                                    }
                                }

                            } else {
                                Toast.makeText(AddEditJobPost.this, "job post update failed. Check the format.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            // Handle the error
                            Log.e("fail: ", t.getLocalizedMessage());
                        }
                    });
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
    }


    private boolean isEmptyField(){

        // no empty fields allowed
        if (jobTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Job title cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (company.getText().toString().isEmpty()){
            Toast.makeText(this, "Company cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (location.getText().toString().isEmpty()){
            Toast.makeText(this, "Location cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (description.getText().toString().isEmpty()){
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (skills.getText().toString().isEmpty()){
            Toast.makeText(this, "Skills cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }


    private void PopulateInputs(JobDTO jobDTO) {
        jobTitle = findViewById(R.id.jobTitleInput);
        company = findViewById(R.id.companyInput);
        workplaceType = findViewById(R.id.workplaceTypeInput);
        location = findViewById(R.id.locationInput);
        employmentType = findViewById(R.id.employmentTypeInput);
        description = findViewById(R.id.descriptionInput);
        skills = findViewById(R.id.jobSkillInput);

        jobTitle.setText(jobDTO.getJobTitle());
        company.setText(jobDTO.getCompany());

        workplaceType.post(() -> {
            // Set the spinner selection based on the current employment type
            workplaceType.setSelection(jobDTO.getWorkplaceType().ordinal());
        });

        location.setText(jobDTO.getJobLocation());

        // Ensure the spinner is populated before setting selection to show correct employment type
        employmentType.post(() -> {
            // Set the spinner selection based on the current employment type
            employmentType.setSelection(jobDTO.getEmploymentType().ordinal());
        });

        description.setText(jobDTO.getDescription());

        List<String> skillNames = new ArrayList<>();
        for (SkillDTO skill: jobDTO.getSkills()){
            skillNames.add(skill.getName());
        }

        skills.setText(String.join(", ", skillNames));
    }
}