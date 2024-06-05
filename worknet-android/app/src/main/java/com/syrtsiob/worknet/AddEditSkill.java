package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syrtsiob.worknet.enums.EmploymentType;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;

public class AddEditSkill extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    String activityMode;

    TextView activityTitle;
    EditText skillDescription;
    SwitchCompat isPrivate;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_skill);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);

        activityTitle = findViewById(R.id.activityTitle);
        if (activityMode.equals(ADD_MODE))
            activityTitle.setText(R.string.add_skill_title);
        else if (activityMode.equals(EDIT_MODE)) {
            activityTitle.setText(R.string.edit_skill_title);
            SkillDTO data = getIntent().getSerializableExtra(SERIALIZABLE, SkillDTO.class);
            if (data != null)
                PopulateInputs(data);
        }

        skillDescription = findViewById(R.id.skillDescriptionInput);
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

    private void PopulateInputs(SkillDTO skillDTO) {
        skillDescription.setText(skillDTO.getName());

        // TODO update these
        // isPrivate.setChecked(workExperienceDTO.getIsPrivate());
    }
}