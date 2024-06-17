package com.syrtsiob.worknet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.services.EducationService;
import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditEducation extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    static final Long EDUCATION_ID = 0L;

    String activityMode;

    Long educationId;

    TextView activityTitle;
    EditText school, degree, fieldOfStudy, startDate, endDate, grade, description;
    SwitchCompat isPublic;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_education);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);
        educationId = getIntent().getLongExtra(EDUCATION_ID.toString(), 0L);

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
        isPublic = findViewById(R.id.isPublicInput);

        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);

        cancelButton.setOnClickListener(listener -> {
            finish();
        });

        submitButton.setOnClickListener(listener -> {

            if (isEmptyField())
                return;

            if (!ValidateDate(startDate.getText().toString(), endDate.getText().toString()))
                return;

            UserDtoResultLiveData.getInstance().observe(this, userDTO -> {
                Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
                EducationService educationService = retrofit.create(EducationService.class);

                SmallUserDTO smallUserDTO = new SmallUserDTO();
                smallUserDTO.setFirstName(userDTO.getFirstName());
                smallUserDTO.setLastName(userDTO.getLastName());
                smallUserDTO.setId(userDTO.getId());

                EducationDTO educationDTO = new EducationDTO();
                boolean isPublicChecked = isPublic.isChecked();
                educationDTO.setIsPublic(isPublicChecked);
                educationDTO.setGrade(grade.getText().toString());
                educationDTO.setDegree(degree.getText().toString());
                educationDTO.setDescription(description.getText().toString());
                educationDTO.setSchool(school.getText().toString());
                educationDTO.setUser(smallUserDTO);
                educationDTO.setFieldOfStudy(fieldOfStudy.getText().toString());
                educationDTO.setStartDate(startDate.getText().toString());
                educationDTO.setEndDate(endDate.getText().toString());

                if (activityMode.equals(ADD_MODE)){
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
                }else if (activityMode.equals(EDIT_MODE)){
                    educationService.updateEducation(educationId, educationDTO).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditEducation.this, "updated education successfully.", Toast.LENGTH_LONG).show();

                                // Replace old education with new one in the userDTO list.
                                ListIterator<EducationDTO> iterator = userDTO.getEducations().listIterator();
                                while (iterator.hasNext()) {
                                    EducationDTO next = iterator.next();
                                    if (next.getId() == educationId) {
                                        //Replace element
                                        educationDTO.setId(educationId);
                                        iterator.set(educationDTO);
                                    }
                                }

                            } else {
                                Toast.makeText(AddEditEducation.this, "education update failed. Check the format.", Toast.LENGTH_LONG).show();
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
    }

    private boolean ValidateDate(String startDateStr, String endDateStr){
        // ensure all dates are dd-MM-yyyy format
        // and that start date is not greater than end date.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            if (startDate.compareTo(endDate) > 0) {
                Toast.makeText(this, "Start date cannot be greater than end date.", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Date format must be dd-mm-yyyy.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean isEmptyField(){
        // no empty fields allowed
        if (school.getText().toString().isEmpty()){
            Toast.makeText(this, "School cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (degree.getText().toString().isEmpty()){
            Toast.makeText(this, "Degree cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (fieldOfStudy.getText().toString().isEmpty()){
            Toast.makeText(this, "Field of study cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (startDate.getText().toString().isEmpty()){
            Toast.makeText(this, "Start date cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (endDate.getText().toString().isEmpty()){
            Toast.makeText(this, "End date cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (grade.getText().toString().isEmpty()){
            Toast.makeText(this, "Grade cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (description.getText().toString().isEmpty()){
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    private void PopulateInputs(EducationDTO educationDTO) {
        school = findViewById(R.id.schoolInput);
        degree = findViewById(R.id.degreeInput);
        fieldOfStudy = findViewById(R.id.fieldOfStudyInput);
        startDate = findViewById(R.id.startDateInput);
        endDate = findViewById(R.id.endDateInput);
        grade = findViewById(R.id.gradeInput);
        description = findViewById(R.id.descriptionInput);
        isPublic = findViewById(R.id.isPublicInput);

        school.setText(educationDTO.getSchool());
        degree.setText(educationDTO.getDegree());
        fieldOfStudy.setText(educationDTO.getFieldOfStudy());
        startDate.setText(educationDTO.getStartDate());
        endDate.setText(educationDTO.getEndDate());
        description.setText(educationDTO.getDescription());
        grade.setText(educationDTO.getGrade());
        isPublic.setChecked(educationDTO.getIsPublic());
    }
}