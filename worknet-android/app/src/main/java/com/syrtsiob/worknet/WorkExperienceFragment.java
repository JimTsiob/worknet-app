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

import com.syrtsiob.worknet.LiveData.ApplicantUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.ApplicantDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.services.UserService;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.WorkExperienceService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WorkExperienceFragment extends Fragment {

    Button addWorkExperienceButton;
    LinearLayout workExperienceList;

    private int requiredItems = 2;

    private int itemsDisplayed = 0; // these two variables are used to prevent duplicate service calls.

    private int workExperienceListSize = 0; // used for showing empty text after all work experiences have been deleted.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_experience, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // if the user wants to see a connection's profile show connection's work experience
        // otherwise show user's work experience (my profile)

        addWorkExperienceButton = requireView().findViewById(R.id.add_work_experience_button);

        ApplicantUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), applicantDTO -> {
            if (applicantDTO != null) {
                addWorkExperienceButton.setVisibility(View.GONE);

                fetchApplicantData(applicantDTO);
            }else{
                ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
                    if (connectionDTO != null){
                        addWorkExperienceButton.setVisibility(View.GONE);

                        fetchConnectionData(connectionDTO);
                    }else{
                        if (itemsDisplayed >= requiredItems){
                            return;
                        }

                        fetchData();
                    }
                });
            }
        });

        itemsDisplayed = 0; // reset in order to have realistic updating in the page.
    }

    public void fetchData(){
        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            workExperienceList = requireView().findViewById(R.id.work_experience_list);
            workExperienceList.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        List<WorkExperienceDTO> workExperiences = response.body().getWorkExperiences();

                        requiredItems = workExperiences.size();
                        workExperienceListSize = workExperiences.size();

                        if (workExperiences.size() == 0){
                            requiredItems = 1;
                        }

                        if (workExperiences.isEmpty()){

                            if (itemsDisplayed >= requiredItems){ // removes duplicates
                                return;
                            }

                            showEmptyWorkExperience();
                        }else{
                            for (WorkExperienceDTO workExperience: workExperiences){
                                if (itemsDisplayed >= requiredItems){
                                    return;
                                }
                                AddWorkExperienceListEntry(workExperience);
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

    public void fetchConnectionData(EnlargedUserDTO enlargedUserDTO){
        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        UserService userService = retrofit.create(UserService.class);

        workExperienceList = requireView().findViewById(R.id.work_experience_list);
        workExperienceList.removeAllViews(); // clear before showing new ones. Removes duplicates

        userService.getUserByEmail(enlargedUserDTO.getEmail()).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()){
                    List<WorkExperienceDTO> workExperiences = response.body().getWorkExperiences();

                    // if connection has no work experiences, or all work experiences are private show empty text
                    if (workExperiences.isEmpty() /*|| isAllPrivateInfo(response.body())*/){
                        showConnectionEmptyWorkExperience();
                    }else{
                        for (WorkExperienceDTO workExperience: workExperiences){
                            //if (workExperience.getIsPublic()){ // show only public work experiences
                                AddConnectionWorkExperienceListEntry(workExperience);
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
    }

    public void fetchApplicantData(ApplicantDTO applicantDTO){
        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        UserService userService = retrofit.create(UserService.class);

        workExperienceList = requireView().findViewById(R.id.work_experience_list);
        workExperienceList.removeAllViews(); // clear before showing new ones. Removes duplicates

        userService.getUserByEmail(applicantDTO.getEmail()).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()){
                    List<WorkExperienceDTO> workExperiences = response.body().getWorkExperiences();

                    // if connection has no work experiences, or all work experiences are private show empty text
                    if (workExperiences.isEmpty() /*|| isAllPrivateInfo(response.body())*/){
                        showApplicantEmptyWorkExperience();
                    }else{
                        for (WorkExperienceDTO workExperience: workExperiences){
                            //if (workExperience.getIsPublic()){ // show only public work experiences
                            AddConnectionWorkExperienceListEntry(workExperience);
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
    }

    /*
     * Method used for showing empty text on connections that have all their info private.
     * */
    public boolean isAllPrivateInfo(UserDTO connection){
        int privateCounter = 0;

        for (WorkExperienceDTO we: connection.getWorkExperiences()){
            if (!we.getIsPublic()){
                privateCounter += 1;
            }
        }

        if (privateCounter == connection.getWorkExperiences().size()){
            return true;
        }

        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addWorkExperienceButton = requireView().findViewById(R.id.add_work_experience_button);
        addWorkExperienceButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditWorkExperience.class);
            intent.putExtra(AddEditWorkExperience.ACTIVITY_MODE, AddEditWorkExperience.ADD_MODE);
            startActivity(intent);
        });
    }

    private void AddConnectionWorkExperienceListEntry(WorkExperienceDTO workExperienceDTO) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View workExperienceListEntry = inflater
                .inflate(R.layout.work_experience_entry_template, workExperienceList, false);

        TextView title = workExperienceListEntry.findViewById(R.id.title);
        TextView employmentType = workExperienceListEntry.findViewById(R.id.employment_type);
        TextView companyName = workExperienceListEntry.findViewById(R.id.company_name);
        TextView location = workExperienceListEntry.findViewById(R.id.location);
        TextView currentlyWorking = workExperienceListEntry.findViewById(R.id.currently_working);
        TextView startDate = workExperienceListEntry.findViewById(R.id.startDate);
        TextView endDate = workExperienceListEntry.findViewById(R.id.endDate);
        TextView description = workExperienceListEntry.findViewById(R.id.description);
        TextView privacy_label = workExperienceListEntry.findViewById(R.id.privacy_label);

        title.setText(workExperienceDTO.getTitle());

        if (workExperienceDTO.getEmploymentType().toString().equals("FULL_TIME")){
            employmentType.setText("Full time position");
        }else if (workExperienceDTO.getEmploymentType().toString().equals("PART_TIME")){
            employmentType.setText("Part time position");
        }else{
            employmentType.setText("Contract position");
        }

        companyName.setText(workExperienceDTO.getCompanyName());
        location.setText(workExperienceDTO.getLocation());

        if (workExperienceDTO.getCurrentlyWorking()){
            currentlyWorking.setText("Currently working on this role");
        }else{
            currentlyWorking.setText("Old position");
        }

        startDate.setText(workExperienceDTO.getStartDate());

        if (workExperienceDTO.getEndDate() != null){
            endDate.setText(workExperienceDTO.getEndDate().toString());
        }else{
            endDate.setText("today");
        }

        description.setText(workExperienceDTO.getDescription());

        if (workExperienceDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = workExperienceListEntry.findViewById(R.id.edit_work_experience_button);
        Button deleteButton = workExperienceListEntry.findViewById(R.id.delete_work_experience_button);

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 50, 0, 0);
        workExperienceListEntry.setLayoutParams(params);

        workExperienceList.addView(workExperienceListEntry);
    }

    private void showEmptyWorkExperience(){
        TextView noWorkExperiencesTextView = new TextView(getActivity());
        noWorkExperiencesTextView.setText("You have no work experience added yet. \n");
        noWorkExperiencesTextView.setTextSize(20); // Set desired text size
        noWorkExperiencesTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(140, 300, 16, 16);
        noWorkExperiencesTextView.setLayoutParams(params);

        workExperienceList.addView(noWorkExperiencesTextView);

        TextView addWorkExperiencesTextView = new TextView(getActivity());
        addWorkExperiencesTextView.setText("Add some with the button above!");
        addWorkExperiencesTextView.setTextSize(20);
        addWorkExperiencesTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(200, 5, 16, 16);
        addWorkExperiencesTextView.setLayoutParams(params2);

        itemsDisplayed += 1;

        workExperienceList.addView(addWorkExperiencesTextView);
    }

    private void showApplicantEmptyWorkExperience(){
        TextView noWorkExperiencesTextView = new TextView(getActivity());
        noWorkExperiencesTextView.setText("This applicant has no work experiences added.");
        noWorkExperiencesTextView.setTextSize(20); // Set desired text size
        noWorkExperiencesTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 300, 16, 16);
        noWorkExperiencesTextView.setLayoutParams(params);

        workExperienceList.addView(noWorkExperiencesTextView);
    }

    private void showConnectionEmptyWorkExperience(){
        TextView noWorkExperiencesTextView = new TextView(getActivity());
        noWorkExperiencesTextView.setText("This connection has no work experiences added.");
        noWorkExperiencesTextView.setTextSize(20); // Set desired text size
        noWorkExperiencesTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 300, 16, 16);
        noWorkExperiencesTextView.setLayoutParams(params);

        workExperienceList.addView(noWorkExperiencesTextView);
    }

    private void AddWorkExperienceListEntry(WorkExperienceDTO workExperienceDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View workExperienceListEntry = inflater
                .inflate(R.layout.work_experience_entry_template, workExperienceList, false);

        TextView title = workExperienceListEntry.findViewById(R.id.title);
        TextView employmentType = workExperienceListEntry.findViewById(R.id.employment_type);
        TextView companyName = workExperienceListEntry.findViewById(R.id.company_name);
        TextView location = workExperienceListEntry.findViewById(R.id.location);
        TextView currentlyWorking = workExperienceListEntry.findViewById(R.id.currently_working);
        TextView startDate = workExperienceListEntry.findViewById(R.id.startDate);
        TextView endDate = workExperienceListEntry.findViewById(R.id.endDate);
        TextView description = workExperienceListEntry.findViewById(R.id.description);
        TextView privacy_label = workExperienceListEntry.findViewById(R.id.privacy_label);

        title.setText(workExperienceDTO.getTitle());
        if (workExperienceDTO.getEmploymentType().toString().equals("FULL_TIME")){
            employmentType.setText("Full time position");
        }else if (workExperienceDTO.getEmploymentType().toString().equals("PART_TIME")){
            employmentType.setText("Part time position");
        }else{
            employmentType.setText("Contract position");
        }

        companyName.setText(workExperienceDTO.getCompanyName());
        location.setText(workExperienceDTO.getLocation());
        startDate.setText(workExperienceDTO.getStartDate().toString());

        if (workExperienceDTO.getEndDate() != null){
            endDate.setText(workExperienceDTO.getEndDate().toString());
        }else{
            endDate.setText("today");
        }

        description.setText(workExperienceDTO.getDescription());

        if (workExperienceDTO.getCurrentlyWorking()){
            currentlyWorking.setText("Currently working on this role");
        }else{
            currentlyWorking.setText("Old position");
        }

        if (workExperienceDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = workExperienceListEntry.findViewById(R.id.edit_work_experience_button);
        Button deleteButton = workExperienceListEntry.findViewById(R.id.delete_work_experience_button);

        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditWorkExperience.class);
            intent.putExtra(AddEditWorkExperience.ACTIVITY_MODE, AddEditWorkExperience.EDIT_MODE);
            intent.putExtra(AddEditWorkExperience.SERIALIZABLE, workExperienceDTO);
            intent.putExtra(AddEditWorkExperience.WORK_EXPERIENCE_ID.toString(), workExperienceDTO.getId());
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {
            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

                Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                WorkExperienceService workExperienceService = retrofit.create(WorkExperienceService.class);

                workExperienceService.deleteWorkExperience(workExperienceDTO.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            workExperienceListSize -= 1;
                            if (workExperienceListSize == 0){ // show empty text if list is empty (dynamically).
                                showEmptyWorkExperience();
                            }
                            Toast.makeText(getActivity(), "deleted work experience successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "work experience deletion failed. Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Handle the error
                        Log.e("fail: ", t.getLocalizedMessage());
                    }
                });
            });
            workExperienceList.removeView(workExperienceListEntry);
        });

        workExperienceList.addView(workExperienceListEntry);
    }
}