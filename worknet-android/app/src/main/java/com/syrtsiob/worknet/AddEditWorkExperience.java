package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.syrtsiob.worknet.enums.EmploymentType;
import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;

public class AddEditWorkExperience extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    String activityMode;

    TextView activityTitle;
    EditText title, companyName, location, startDate, endDate, description;
    Spinner employmentType;
    SwitchCompat currentlyWorking, isPrivate;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_work_experience);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);

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
        isPrivate = findViewById(R.id.isPrivateInput);

        currentlyWorking = findViewById(R.id.currentlyWorkingInput);
        currentlyWorking.setOnCheckedChangeListener(
                (buttonView, isChecked) -> endDate.setEnabled(!isChecked));

        employmentType = findViewById(R.id.employmentTypeInput);
        String[] items = EmploymentType.getEmploymentTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        employmentType.setAdapter(adapter);

        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);

        cancelButton.setOnClickListener(listener -> {
            finish();
        });

        submitButton.setOnClickListener(listener -> {
            // TODO upload to database
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

    private void PopulateInputs(WorkExperienceDTO workExperienceDTO) {
        title.setText(workExperienceDTO.getTitle());
        companyName.setText(workExperienceDTO.getCompanyName());
        location.setText(workExperienceDTO.getLocation());
        startDate.setText(workExperienceDTO.getStartDate().toString());
        endDate.setText(workExperienceDTO.getEndDate().toString());
        description.setText(workExperienceDTO.getDescription());

        currentlyWorking.setChecked(workExperienceDTO.isCurrentlyWorking());

        employmentType.setSelection(workExperienceDTO.getEmploymentType().ordinal());

        // TODO update these
        // isPrivate.setChecked(workExperienceDTO.getIsPrivate());
    }
}