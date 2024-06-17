package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.model.JobDTO;

public class JobPostingDetails extends AppCompatActivity {

    static final String SERIALIZABLE = "serializable";

    TextView jobTitle, company, workplaceType, location, employmentType;
    Button returnButton, applyButton;

    JobDTO jobDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting_details);

        jobDTO = getIntent().getSerializableExtra(SERIALIZABLE, JobDTO.class);

        jobTitle = findViewById(R.id.jobTitle);
        company = findViewById(R.id.company);
        workplaceType = findViewById(R.id.workplaceType);
        location = findViewById(R.id.jobLocation);
        employmentType = findViewById(R.id.employmentType);

        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(listener -> {
            finish();
        });

        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(listener -> {
            // TODO database calls
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
        // TODO database calls
    }
}