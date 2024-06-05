package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syrtsiob.worknet.model.EducationDTO;

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
        startDate.setText(educationDTO.getStartDate().toString());
        endDate.setText(educationDTO.getEndDate().toString());
        description.setText(educationDTO.getDescription());

        // TODO update these
        // grade.setText(educationDTO.getGrade().toString());
        // isPrivate.setText(educationDTO.getIsPrivate());
    }
}