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
import com.syrtsiob.worknet.services.SkillService;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditSkill extends AppCompatActivity {

    static final String EDIT_MODE = "edit";
    static final String ADD_MODE = "add";
    static final String ACTIVITY_MODE = "activity_mode";
    static final String SERIALIZABLE = "serializable";

    static final Long SKILL_ID = 0L;

    String activityMode;

    Long skillId;

    TextView activityTitle;
    EditText skillDescription;
    SwitchCompat isPublic;
    Button cancelButton, submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_skill);

        activityMode = getIntent().getStringExtra(ACTIVITY_MODE);
        skillId = getIntent().getLongExtra(SKILL_ID.toString(), 0L);

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
        isPublic = findViewById(R.id.isPublicInput);

        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);

        cancelButton.setOnClickListener(listener -> {
            finish();
        });

        submitButton.setOnClickListener(listener -> {
            // TODO upload to database
            if (isEmptyField())
                return;

            UserDtoResultLiveData.getInstance().observe(this, userDTO -> {
                Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
                SkillService skillService = retrofit.create(SkillService.class);

                SmallUserDTO smallUserDTO = new SmallUserDTO();
                smallUserDTO.setFirstName(userDTO.getFirstName());
                smallUserDTO.setLastName(userDTO.getLastName());
                smallUserDTO.setId(userDTO.getId());

                SkillDTO skillDTO = new SkillDTO();
                boolean isPublicChecked = isPublic.isChecked();
                skillDTO.setIsPublic(isPublicChecked);
                skillDTO.setName(skillDescription.getText().toString());
                skillDTO.setUser(smallUserDTO);

                if (activityMode.equals(ADD_MODE)){
                    skillService.addSkill(skillDTO, userDTO.getEmail()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditSkill.this, "added skill successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddEditSkill.this, "skill addition failed. Check the format.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("fail: ", t.getLocalizedMessage());
                            // Handle the error
                            Toast.makeText(AddEditSkill.this, "skill addition failed. Server failure.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else if (activityMode.equals(EDIT_MODE)){
                    skillService.updateSkill(skillId, skillDTO).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditSkill.this, "updated skill successfully.", Toast.LENGTH_LONG).show();

                                // Replace old skill with new one in the userDTO list.
                                ListIterator<SkillDTO> iterator = userDTO.getSkills().listIterator();
                                while (iterator.hasNext()) {
                                    SkillDTO next = iterator.next();
                                    if (next.getId() == skillId) {
                                        //Replace element
                                        skillDTO.setId(skillId);
                                        iterator.set(skillDTO);
                                    }
                                }

                            } else {
                                Toast.makeText(AddEditSkill.this, "skill update failed. Check the format.", Toast.LENGTH_LONG).show();
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

    private boolean isEmptyField(){
        // no empty fields allowed
        if (skillDescription.getText().toString().isEmpty()){
            Toast.makeText(this, "Skill name cannot be empty.", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    private void PopulateInputs(SkillDTO skillDTO) {
        skillDescription = findViewById(R.id.skillDescriptionInput);
        isPublic = findViewById(R.id.isPublicInput);

        skillDescription.setText(skillDTO.getName());
        isPublic.setChecked(skillDTO.getIsPublic());
    }
}