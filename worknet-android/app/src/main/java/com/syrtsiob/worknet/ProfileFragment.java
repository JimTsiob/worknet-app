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

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    Button addEducationButton;
    LinearLayout educationList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
        grade.setText(educationDTO.getGrade().toString());
        description.setText(educationDTO.getDescription());
        // TODO update grade and privacy label
        //privacy_label.setText(educationDTO.get());

        Button editButton = educationList.findViewById(R.id.edit_education_button);
        Button deleteButton = educationList.findViewById(R.id.delete_education_button);

        editButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        educationList.addView(educationListEntry);
    }

    private void AddEducationListEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View educationListEntry = inflater
                .inflate(R.layout.education_entry_template, educationList, false);
        educationList.addView(educationListEntry);
    }
}