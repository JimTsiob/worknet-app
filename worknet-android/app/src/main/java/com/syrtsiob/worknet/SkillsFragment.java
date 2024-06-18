package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.services.SkillService;
import com.syrtsiob.worknet.services.UserService;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SkillsFragment extends Fragment {

    Button addSkillButton;
    LinearLayout skillList;

    private int requiredItems = 2;

    private int itemsDisplayed = 0; // these two variables are used to prevent duplicate service calls.

    private int skillListSize = 0; // used for showing empty text after all skills have been deleted.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skills, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // if the user wants to see a connection's profile show connection's skills
        // otherwise show user's skills (my profile)


        addSkillButton = requireView().findViewById(R.id.add_skills_button);

        ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
            if (connectionDTO != null){
                addSkillButton.setVisibility(View.GONE);

                fetchConnectionData();
            }else{
                if (itemsDisplayed >= requiredItems){
                    return;
                }

                fetchData();
            }
        });

        itemsDisplayed = 0; // reset in order to have realistic updating in the page.
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addSkillButton = requireView().findViewById(R.id.add_skills_button);
        addSkillButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditSkill.class);
            intent.putExtra(AddEditSkill.ACTIVITY_MODE, AddEditSkill.ADD_MODE);
            startActivity(intent);
        });

        skillList = requireView().findViewById(R.id.skills_list);
    }

    public void fetchData(){
        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            skillList = requireView().findViewById(R.id.skills_list);
            skillList.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        List<SkillDTO> skills  = response.body().getSkills();

                        requiredItems = skills.size();
                        skillListSize = skills.size();

                        if (skills.size() == 0){
                            requiredItems = 1;
                        }

                        if (skills.isEmpty()){
                            if (itemsDisplayed >= requiredItems){ // removes duplicates
                                return;
                            }

                            showEmptySkills();
                        }else{
                            for (SkillDTO skill: skills){
                                if (itemsDisplayed >= requiredItems){
                                    return;
                                }
                                AddSkillListEntry(skill);
                                itemsDisplayed += 1;
                            }
                        }
                    }else{
                        Log.d("format fail:", "something bad happened here.");
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("Server fail:", t.getLocalizedMessage());
                }
            });

        });
    }

    public void fetchConnectionData(){
        ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            skillList = requireView().findViewById(R.id.skills_list);
            skillList.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(connectionDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        List<SkillDTO> skills = response.body().getSkills();

                        // if connection has no skills, or all skills are private show empty text
                        if (skills.isEmpty() /*|| isAllPrivateInfo(response.body())*/){
                            showConnectionEmptySkills();
                        }else{
                            for (SkillDTO skill: skills){
                                //if (skill.getIsPublic()){ // show only public skills
                                    AddConnectionSkillListEntry(skill);
                                //}
                            }
                        }
                    }else{
                        Log.d("format fail:", "something bad happened here.");
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("Server fail:", t.getLocalizedMessage());
                }
            });
        });
    }

    /*
     * Method used for showing empty text on unconnected users that have all their info private.
     * */
    public boolean isAllPrivateInfo(UserDTO connection){
        int privateCounter = 0;

        for (SkillDTO skill: connection.getSkills()){
            if (!skill.getIsPublic()){
                privateCounter += 1;
            }
        }

        if (privateCounter == connection.getSkills().size()){
            return true;
        }

        return false;
    }

    private void AddConnectionSkillListEntry(SkillDTO skillDTO) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View skillListEntry = inflater
                .inflate(R.layout.skills_entry_template, skillList, false);

        TextView skillName = skillListEntry.findViewById(R.id.skill_name);
        TextView privacy_label = skillListEntry.findViewById(R.id.privacy_label);

        skillName.setText(skillDTO.getName());

        if (skillDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = skillListEntry.findViewById(R.id.edit_skill_button);
        Button deleteButton = skillListEntry.findViewById(R.id.delete_skill_button);

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 50, 0, 0);
        skillListEntry.setLayoutParams(params);

        skillList.addView(skillListEntry);
    }

    private void showConnectionEmptySkills(){
        TextView noSkillsTextView = new TextView(getActivity());
        noSkillsTextView.setText("This connection has no skills added.");
        noSkillsTextView.setTextSize(20); // Set desired text size
        noSkillsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(200, 300, 16, 16);
        noSkillsTextView.setLayoutParams(params);

        skillList.addView(noSkillsTextView);
    }

    private void showEmptySkills(){
        TextView noSkillsTextView = new TextView(getActivity());
        noSkillsTextView.setText("You have no skills added yet. \n");
        noSkillsTextView.setTextSize(20); // Set desired text size
        noSkillsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(250, 300, 16, 16);
        noSkillsTextView.setLayoutParams(params);

        skillList.addView(noSkillsTextView);

        TextView addSkillsTextView = new TextView(getActivity());
        addSkillsTextView.setText("Add some with the button above!");
        addSkillsTextView.setTextSize(20);
        addSkillsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(200, 5, 16, 16);
        addSkillsTextView.setLayoutParams(params2);

        itemsDisplayed += 1;

        skillList.addView(addSkillsTextView);
    }

    private void AddSkillListEntry(SkillDTO skillDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View skillListEntry = inflater
                .inflate(R.layout.skills_entry_template, skillList, false);

        TextView name = skillListEntry.findViewById(R.id.skill_name);
        TextView privacy_label = skillListEntry.findViewById(R.id.privacy_label);

        name.setText(skillDTO.getName());

        if (skillDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = skillListEntry.findViewById(R.id.edit_skill_button);
        Button deleteButton = skillListEntry.findViewById(R.id.delete_skill_button);

        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditSkill.class);
            intent.putExtra(AddEditSkill.ACTIVITY_MODE, AddEditSkill.EDIT_MODE);
            intent.putExtra(AddEditSkill.SERIALIZABLE, skillDTO);
            intent.putExtra(AddEditSkill.SKILL_ID.toString(), skillDTO.getId());
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {

            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

                Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                SkillService skillService = retrofit.create(SkillService.class);

                skillService.deleteSkill(skillDTO.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            skillListSize -= 1;
                            if (skillListSize == 0){ // show empty text if list is empty (dynamically).
                                showEmptySkills();
                            }
                            Toast.makeText(getActivity(), "deleted skill successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "skill deletion failed. Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Handle the error
                        Log.e("fail: ", t.getLocalizedMessage());
                    }
                });
            });

            skillList.removeView(skillListEntry);

        });

        skillList.addView(skillListEntry);
    }
}