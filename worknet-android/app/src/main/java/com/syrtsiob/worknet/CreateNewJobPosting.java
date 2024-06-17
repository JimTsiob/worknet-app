package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.syrtsiob.worknet.enums.EmploymentType;
import com.syrtsiob.worknet.enums.WorkplaceType;

public class CreateNewJobPosting extends AppCompatActivity {

    EditText jobTitle, company, location;
    Spinner workplaceType, employmentType;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_job_posting);

        jobTitle = findViewById(R.id.jobTitleInput);
        company = findViewById(R.id.companyInput);
        location = findViewById(R.id.locationInput);

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
}