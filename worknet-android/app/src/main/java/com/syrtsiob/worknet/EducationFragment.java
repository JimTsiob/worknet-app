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
import com.syrtsiob.worknet.services.EducationService;
import com.syrtsiob.worknet.services.UserService;
import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class EducationFragment extends Fragment {

    Button addEducationButton;
    LinearLayout educationList;

    private int requiredItems = 2;

    private int itemsDisplayed = 0; // these two variables are used to prevent duplicate service calls.

    private int educationListSize = 0; // used for showing empty text after all educations have been deleted.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_education, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // if the user wants to see a connection's profile show connection's education
        // otherwise show user's education (my profile)


        addEducationButton = requireView().findViewById(R.id.add_education_button);

        ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
            if (connectionDTO != null){
                addEducationButton.setVisibility(View.GONE);

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


    public void fetchData(){
        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            educationList = requireView().findViewById(R.id.education_list);
            educationList.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        List<EducationDTO> educations = response.body().getEducations();

                        requiredItems = educations.size();
                        educationListSize = educations.size();

                        if (educations.size() == 0){
                            requiredItems = 1;
                        }

                        if (educations.isEmpty()){

                            if (itemsDisplayed >= requiredItems){ // removes duplicates
                                return;
                            }

                            showEmptyEducation();
                        }else{
                            for (EducationDTO education: educations){
                                if (itemsDisplayed >= requiredItems){
                                    return;
                                }
                                AddEducationListEntry(education);
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

            educationList = requireView().findViewById(R.id.education_list);
            educationList.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(connectionDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        List<EducationDTO> educations = response.body().getEducations();

                        // if connection has no educations, or all educations are private show empty text
                        if (educations.isEmpty() /*|| isAllPrivateInfo(response.body())*/){
                            showConnectionEmptyEducation();
                        }else{
                            for (EducationDTO education: educations){
                                //if (education.getIsPublic()){ // show only public educations
                                    AddConnectionEducationListEntry(education);
                               // }
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
    * Method used for showing empty text on connections that have all their info private.
    * */
    public boolean isAllPrivateInfo(UserDTO connection){
        int privateCounter = 0;

        for (EducationDTO e: connection.getEducations()){
            if (!e.getIsPublic()){
                privateCounter += 1;
            }
        }

        if (privateCounter == connection.getEducations().size()){
            return true;
        }

        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addEducationButton = requireView().findViewById(R.id.add_education_button);
        addEducationButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditEducation.class);
            intent.putExtra(AddEditEducation.ACTIVITY_MODE, AddEditEducation.ADD_MODE);
            startActivity(intent);
        });
    }

    private void AddConnectionEducationListEntry(EducationDTO educationDTO) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View educationListEntry = inflater
                .inflate(R.layout.education_entry_template, educationList, false);

        TextView school = educationListEntry.findViewById(R.id.school);
        TextView degree = educationListEntry.findViewById(R.id.degree);
        TextView fieldOfStudy = educationListEntry.findViewById(R.id.fieldOfStudy);
        TextView startDate = educationListEntry.findViewById(R.id.startDate);
        TextView endDate = educationListEntry.findViewById(R.id.endDate);
        TextView grade = educationListEntry.findViewById(R.id.grade);
        TextView description = educationListEntry.findViewById(R.id.description);
        TextView privacy_label = educationListEntry.findViewById(R.id.privacy_label);

        school.setText(educationDTO.getSchool());
        degree.setText(educationDTO.getDegree());
        fieldOfStudy.setText(educationDTO.getFieldOfStudy());
        startDate.setText(educationDTO.getStartDate());
        endDate.setText(educationDTO.getEndDate());
        description.setText(educationDTO.getDescription());

        grade.setText(educationDTO.getGrade());
        if (educationDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = educationListEntry.findViewById(R.id.edit_education_button);
        Button deleteButton = educationListEntry.findViewById(R.id.delete_education_button);

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 50, 0, 0);
        educationListEntry.setLayoutParams(params);

        educationList.addView(educationListEntry);
    }

    private void showEmptyEducation(){
        TextView noEducationsTextView = new TextView(getActivity());
        noEducationsTextView.setText("You have no education added yet. \n");
        noEducationsTextView.setTextSize(20); // Set desired text size
        noEducationsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(200, 300, 16, 16);
        noEducationsTextView.setLayoutParams(params);

        educationList.addView(noEducationsTextView);

        TextView addEducationsTextView = new TextView(getActivity());
        addEducationsTextView.setText("Add some with the button above!");
        addEducationsTextView.setTextSize(20);
        addEducationsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(200, 5, 16, 16);
        addEducationsTextView.setLayoutParams(params2);

        itemsDisplayed += 1;

        educationList.addView(addEducationsTextView);
    }

    private void showConnectionEmptyEducation(){
        TextView noEducationsTextView = new TextView(getActivity());
        noEducationsTextView.setText("This connection has no educations added.");
        noEducationsTextView.setTextSize(20); // Set desired text size
        noEducationsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 300, 16, 16);
        noEducationsTextView.setLayoutParams(params);

        educationList.addView(noEducationsTextView);
    }

    private void AddEducationListEntry(EducationDTO educationDTO) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View educationListEntry = inflater
                .inflate(R.layout.education_entry_template, educationList, false);

        TextView school = educationListEntry.findViewById(R.id.school);
        TextView degree = educationListEntry.findViewById(R.id.degree);
        TextView fieldOfStudy = educationListEntry.findViewById(R.id.fieldOfStudy);
        TextView startDate = educationListEntry.findViewById(R.id.startDate);
        TextView endDate = educationListEntry.findViewById(R.id.endDate);
        TextView grade = educationListEntry.findViewById(R.id.grade);
        TextView description = educationListEntry.findViewById(R.id.description);
        TextView privacy_label = educationListEntry.findViewById(R.id.privacy_label);

        school.setText(educationDTO.getSchool());
        degree.setText(educationDTO.getDegree());
        fieldOfStudy.setText(educationDTO.getFieldOfStudy());
        startDate.setText(educationDTO.getStartDate());
        endDate.setText(educationDTO.getEndDate());
        description.setText(educationDTO.getDescription());

        grade.setText(educationDTO.getGrade());
        if (educationDTO.getIsPublic()){
            privacy_label.setText("Public information");
        }else{
            privacy_label.setText("This information is set to private.");
        }

        Button editButton = educationListEntry.findViewById(R.id.edit_education_button);
        Button deleteButton = educationListEntry.findViewById(R.id.delete_education_button);

        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditEducation.class);
            intent.putExtra(AddEditEducation.ACTIVITY_MODE, AddEditEducation.EDIT_MODE);
            intent.putExtra(AddEditEducation.SERIALIZABLE, educationDTO);
            intent.putExtra(AddEditEducation.EDUCATION_ID.toString(), educationDTO.getId());
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {

            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

                Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                EducationService educationService = retrofit.create(EducationService.class);

                educationService.deleteEducation(educationDTO.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            educationListSize -= 1;
                            if (educationListSize == 0){ // show empty text if list is empty (dynamically).
                                showEmptyEducation();
                            }
                            Toast.makeText(getActivity(), "deleted education successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "education deletion failed. Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Handle the error
                        Log.e("fail: ", t.getLocalizedMessage());
                    }
                });
            });

            educationList.removeView(educationListEntry);

        });

        educationList.addView(educationListEntry);
    }
}