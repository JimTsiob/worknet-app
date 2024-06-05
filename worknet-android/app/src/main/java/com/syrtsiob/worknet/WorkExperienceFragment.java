package com.syrtsiob.worknet;

import android.content.Intent;
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
import com.syrtsiob.worknet.model.WorkExperienceDTO;

public class WorkExperienceFragment extends Fragment {

    Button addWorkExperienceButton;
    LinearLayout workExperienceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_experience, container, false);
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

        workExperienceList = requireView().findViewById(R.id.work_experience_list);

        AddWorkExperienceListEntry();
        AddWorkExperienceListEntry();
        AddWorkExperienceListEntry();
    }

    // TODO this is for testing purposes -- remove
    private void AddWorkExperienceListEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View workExperienceListEntry = inflater
                .inflate(R.layout.work_experience_entry_template, workExperienceList, false);

        Button editButton = workExperienceListEntry.findViewById(R.id.edit_work_experience_button);
        Button deleteButton = workExperienceListEntry.findViewById(R.id.delete_work_experience_button);

        editButton.setOnClickListener(listener -> {

        });

        deleteButton.setOnClickListener(listener -> {
            workExperienceList.removeView(workExperienceListEntry);
        });

        workExperienceList.addView(workExperienceListEntry);
    }

    private void AddEducationListEntry(WorkExperienceDTO workExperienceDTO) {
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
        employmentType.setText(workExperienceDTO.getEmploymentType().toString());
        companyName.setText(workExperienceDTO.getCompanyName());
        location.setText(workExperienceDTO.getLocation());
        startDate.setText(workExperienceDTO.getStartDate().toString());
        endDate.setText(workExperienceDTO.getEndDate().toString());
        description.setText(workExperienceDTO.getDescription());

        // TODO update currently working and privacy labels
        // privacy_label.setText(workExperienceDTO.get());
        // currentlyWorking.setText(workExperienceDTO.getGrade().toString());

        Button editButton = workExperienceListEntry.findViewById(R.id.edit_work_experience_button);
        Button deleteButton = workExperienceListEntry.findViewById(R.id.delete_work_experience_button);

        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditWorkExperience.class);
            intent.putExtra(AddEditWorkExperience.ACTIVITY_MODE, AddEditWorkExperience.EDIT_MODE);
            intent.putExtra(AddEditWorkExperience.SERIALIZABLE, workExperienceDTO);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            workExperienceList.removeView(workExperienceListEntry);
        });

        workExperienceList.addView(workExperienceListEntry);
    }
}