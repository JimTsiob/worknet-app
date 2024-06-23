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
import androidx.appcompat.widget.SwitchCompat;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.EmploymentType;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.WorkExperienceService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditWorkExperience extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    static final Long WORK_EXPERIENCE_ID = 0L;

    String activityMode;

    Long workExperienceId;

    TextView activityTitle;
    EditText title, companyName, location, startDate, endDate, description;
    Spinner employmentType;
    SwitchCompat currentlyWorking, isPublic;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_work_experience);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);
        workExperienceId = getIntent().getLongExtra(WORK_EXPERIENCE_ID.toString(), 0L);

        activityTitle = findViewById(R.id.activityTitle);
        if (activityMode.equals(ADD_MODE))
            activityTitle.setText(R.string.add_work_experience_title);
        else if (activityMode.equals(EDIT_MODE)) {
            activityTitle.setText(R.string.edit_work_experience_title);
            WorkExperienceDTO data = getIntent().getSerializableExtra(SERIALIZABLE, WorkExperienceDTO.class);
            if (data != null)
                PopulateInputs(data);
        }

        title = findViewById(R.id.titleInput);
        companyName = findViewById(R.id.companyNameInput);
        location = findViewById(R.id.locationInput);
        startDate = findViewById(R.id.startDateInput);
        endDate = findViewById(R.id.endDateInput);
        description = findViewById(R.id.descriptionInput);
        isPublic = findViewById(R.id.isPublicInput);

        currentlyWorking = findViewById(R.id.currentlyWorkingInput);
        currentlyWorking.setOnCheckedChangeListener(
                (buttonView, isChecked) -> endDate.setEnabled(!isChecked));

        employmentType = findViewById(R.id.employmentTypeInput);
        String[] items = EmploymentType.getEmploymentTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        employmentType.setAdapter(adapter);
        WorkExperienceDTO workExperienceDTO = new WorkExperienceDTO();

        // This ensures that the dropdown will have the correct input for the DB and for update.
        employmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Convert the selected item back to the corresponding EmploymentType enum
                EmploymentType selectedEmploymentType = EmploymentType.valueOf(selectedItem);

                workExperienceDTO.setEmploymentType(selectedEmploymentType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Convert the selected item back to the corresponding EmploymentType enum
                EmploymentType selectedEmploymentType = EmploymentType.valueOf("FULL_TIME");

                workExperienceDTO.setEmploymentType(selectedEmploymentType);
            }
        });



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
                WorkExperienceService workExperienceService = retrofit.create(WorkExperienceService.class);

                SmallUserDTO smallUserDTO = new SmallUserDTO();
                smallUserDTO.setFirstName(userDTO.getFirstName());
                smallUserDTO.setLastName(userDTO.getLastName());
                smallUserDTO.setId(userDTO.getId());

                boolean isPublicChecked = isPublic.isChecked();
                workExperienceDTO.setIsPublic(isPublicChecked);
                boolean isCurrentlyWorkingChecked = currentlyWorking.isChecked();
                workExperienceDTO.setCurrentlyWorking(isCurrentlyWorkingChecked);
                workExperienceDTO.setDescription(description.getText().toString());
                workExperienceDTO.setCompanyName(companyName.getText().toString());
                workExperienceDTO.setTitle(title.getText().toString());
                workExperienceDTO.setLocation(location.getText().toString());



                workExperienceDTO.setUser(smallUserDTO);

                workExperienceDTO.setStartDate(startDate.getText().toString());
                workExperienceDTO.setEndDate(endDate.getText().toString());

                if (activityMode.equals(ADD_MODE)){
                    workExperienceService.addWorkExperience(workExperienceDTO, userDTO.getEmail()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditWorkExperience.this, "added work experience successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddEditWorkExperience.this, "work experience addition failed. You cannot add the same work experience twice.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("fail: ", t.getLocalizedMessage());
                            // Handle the error
                            Toast.makeText(AddEditWorkExperience.this, "work experience addition failed. Server failure.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else if (activityMode.equals(EDIT_MODE)){
                    workExperienceService.updateWorkExperience(workExperienceId, workExperienceDTO).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditWorkExperience.this, "updated work experience successfully.", Toast.LENGTH_LONG).show();

                                // Replace old work experience with new one in the userDTO list.
                                ListIterator<WorkExperienceDTO> iterator = userDTO.getWorkExperiences().listIterator();
                                while (iterator.hasNext()) {
                                    WorkExperienceDTO next = iterator.next();
                                    if (next.getId() == workExperienceId) {
                                        //Replace element
                                        workExperienceDTO.setId(workExperienceId);
                                        iterator.set(workExperienceDTO);
                                    }
                                }

                            } else {
                                Toast.makeText(AddEditWorkExperience.this, "work experience update failed. Check the format.", Toast.LENGTH_LONG).show();
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

        currentlyWorking = findViewById(R.id.currentlyWorkingInput);
        boolean isCurrentlyWorkingChecked = currentlyWorking.isChecked();
        // no empty fields allowed
        if (title.getText().toString().isEmpty()){
            Toast.makeText(this, "Title cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (companyName.getText().toString().isEmpty()){
            Toast.makeText(this, "Company name cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (location.getText().toString().isEmpty()){
            Toast.makeText(this, "Location cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (startDate.getText().toString().isEmpty()){
            Toast.makeText(this, "Start date cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        if (!isCurrentlyWorkingChecked){ // if currently working is checked this field should be null.
            if (endDate.getText().toString().isEmpty()){
                Toast.makeText(this, "End date cannot be empty.", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        if (description.getText().toString().isEmpty()){
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    private boolean ValidateDate(String startDateStr, String endDateStr){
        // ensure all dates are dd-MM-yyyy format
        // and that start date is not greater than end date.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        currentlyWorking = findViewById(R.id.currentlyWorkingInput);
        boolean isCurrentlyWorkingChecked = currentlyWorking.isChecked();

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            if (!isCurrentlyWorkingChecked){
                if (startDate.compareTo(endDate) > 0) {
                    Toast.makeText(this, "Start date cannot be greater than end date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch (ParseException e) {
            if (isCurrentlyWorkingChecked){
                return true;
            }
            Toast.makeText(this, "Date format must be dd-mm-yyyy.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void PopulateInputs(WorkExperienceDTO workExperienceDTO) {
        title = findViewById(R.id.titleInput);
        companyName = findViewById(R.id.companyNameInput);
        location = findViewById(R.id.locationInput);
        startDate = findViewById(R.id.startDateInput);
        endDate = findViewById(R.id.endDateInput);
        description = findViewById(R.id.descriptionInput);
        isPublic = findViewById(R.id.isPublicInput);
        currentlyWorking = findViewById(R.id.currentlyWorkingInput);
        employmentType = findViewById(R.id.employmentTypeInput);

        title.setText(workExperienceDTO.getTitle());
        companyName.setText(workExperienceDTO.getCompanyName());
        location.setText(workExperienceDTO.getLocation());
        startDate.setText(workExperienceDTO.getStartDate());
        endDate.setText(workExperienceDTO.getEndDate());
        description.setText(workExperienceDTO.getDescription());

        currentlyWorking.setChecked(workExperienceDTO.getCurrentlyWorking());

        // Ensure the spinner is populated before setting selection to show correct employment type
        employmentType.post(() -> {
            // Set the spinner selection based on the current employment type
            employmentType.setSelection(workExperienceDTO.getEmploymentType().ordinal());
        });

        isPublic.setChecked(workExperienceDTO.getIsPublic());
    }
}