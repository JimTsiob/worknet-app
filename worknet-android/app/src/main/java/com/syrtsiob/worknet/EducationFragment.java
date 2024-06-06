package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.EducationDTO;

import java.util.List;


public class EducationFragment extends Fragment {

    Button addEducationButton;
    LinearLayout educationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_education, container, false);
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

        educationList = requireView().findViewById(R.id.education_list);

        UserDtoResultLiveData.getInstance().observe(getActivity(), userDTO -> {
            List<EducationDTO> educations = userDTO.getEducations();

            if (educations.isEmpty()){
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

                educationList.addView(addEducationsTextView);
            }else{
                for (EducationDTO education: educations){
                    AddEducationListEntry(education);
                }
            }

        });
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
        if (educationDTO.getPublic()){
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
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            educationList.removeView(educationListEntry);
        });

        educationList.addView(educationListEntry);
    }
}