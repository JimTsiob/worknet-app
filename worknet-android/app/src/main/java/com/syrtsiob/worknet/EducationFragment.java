package com.syrtsiob.worknet;

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

import com.syrtsiob.worknet.model.EducationDTO;


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
        educationList = requireView().findViewById(R.id.education_list);

        AddEducationListEntry();
        AddEducationListEntry();
        AddEducationListEntry();
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
        startDate.setText(educationDTO.getStartDate().toString());
        endDate.setText(educationDTO.getEndDate().toString());
        description.setText(educationDTO.getDescription());

        // TODO update grade and privacy label
        // grade.setText(educationDTO.getGrade().toString());
        // privacy_label.setText(educationDTO.get());

        Button editButton = educationListEntry.findViewById(R.id.edit_education_button);
        Button deleteButton = educationListEntry.findViewById(R.id.delete_education_button);

        editButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            educationList.removeView(educationListEntry);
        });

        educationList.addView(educationListEntry);
    }

    // TODO this is for testing purposes -- remove
    private void AddEducationListEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View educationListEntry = inflater
                .inflate(R.layout.education_entry_template, educationList, false);

        Button editButton = educationListEntry.findViewById(R.id.edit_education_button);
        Button deleteButton = educationListEntry.findViewById(R.id.delete_education_button);

        editButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            educationList.removeView(educationListEntry);
        });

        educationList.addView(educationListEntry);
    }
}