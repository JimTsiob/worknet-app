package com.syrtsiob.worknet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.interfaces.EducationService;
import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditEducation extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    String activityMode;

    TextView activityTitle;
    EditText school, degree, fieldOfStudy, startDate, endDate, grade, description;
    SwitchCompat isPrivate;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_education);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);

        activityTitle = findViewById(R.id.activityTitle);
        if (activityMode.equals(ADD_MODE))
            activityTitle.setText(R.string.add_education_title);
        else if (activityMode.equals(EDIT_MODE)) {
            activityTitle.setText(R.string.edit_education_title);
            EducationDTO data = getIntent().getSerializableExtra(SERIALIZABLE, EducationDTO.class);
            if (data != null)
                PopulateInputs(data);
        }

        school = findViewById(R.id.schoolInput);
        degree = findViewById(R.id.degreeInput);
        fieldOfStudy = findViewById(R.id.fieldOfStudyInput);
        startDate = findViewById(R.id.startDateInput);
        endDate = findViewById(R.id.endDateInput);
        grade = findViewById(R.id.gradeInput);
        description = findViewById(R.id.descriptionInput);
        isPrivate = findViewById(R.id.isPrivateInput);

        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);

        cancelButton.setOnClickListener(listener -> {
            finish();
        });

        submitButton.setOnClickListener(listener -> {
            // TODO upload to database
            UserDtoResultLiveData.getInstance().observe(this, userDTO -> {
                Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
                EducationService educationService = retrofit.create(EducationService.class);

                SmallUserDTO smallUserDTO = new SmallUserDTO();
                smallUserDTO.setFirstName(userDTO.getFirstName());
                smallUserDTO.setLastName(userDTO.getLastName());
                smallUserDTO.setId(userDTO.getId());

                EducationDTO educationDTO = new EducationDTO();
                boolean isPrivateChecked = isPrivate.isChecked();
                educationDTO.setPublic(isPrivateChecked);
                educationDTO.setGrade(grade.getText().toString());
                educationDTO.setDegree(degree.getText().toString());
                educationDTO.setDescription(description.getText().toString());
                educationDTO.setSchool(school.getText().toString());
                educationDTO.setUser(smallUserDTO);
                educationDTO.setFieldOfStudy(fieldOfStudy.getText().toString());
                educationDTO.setStartDate(startDate.getText().toString());
                educationDTO.setEndDate(endDate.getText().toString());

                educationService.addEducation(educationDTO, userDTO.getEmail()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddEditEducation.this, "added education successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddEditEducation.this, "education addition failed. Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("fail: ", t.getLocalizedMessage());
                        // Handle the error
                        Toast.makeText(AddEditEducation.this, "education addition failed. Server failure.", Toast.LENGTH_LONG).show();
                    }
                });
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

    private void PopulateInputs(EducationDTO educationDTO) {
        school.setText(educationDTO.getSchool());
        degree.setText(educationDTO.getDegree());
        fieldOfStudy.setText(educationDTO.getFieldOfStudy());
        startDate.setText(educationDTO.getStartDate());
        endDate.setText(educationDTO.getEndDate());
        description.setText(educationDTO.getDescription());
        grade.setText(educationDTO.getGrade());
        isPrivate.setChecked(educationDTO.getPublic());
    }
}