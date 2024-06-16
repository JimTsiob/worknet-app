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

import com.syrtsiob.worknet.model.JobDTO;

public class ViewMyJobPostingsFragment extends Fragment {

    Button createJobPostingButton;
    LinearLayout viewJobPostingsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_my_job_postings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewJobPostingsContainer = requireView().findViewById(R.id.viewMyJobPostingsContainer);

        createJobPostingButton = requireView().findViewById(R.id.createJobPosting);
        createJobPostingButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), CreateNewJobPosting.class);
            startActivity(intent);
        });

        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
    }

    // TODO this is for testing purposes -- remove
    private void AddJobPostingEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View jobPostingEntry = inflater
                .inflate(R.layout.job_posting_entry, viewJobPostingsContainer, false);

        Button deleteButton = jobPostingEntry.findViewById(R.id.jobPostingButton);
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(listener -> {
            viewJobPostingsContainer.removeView(jobPostingEntry);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }

    private void AddJobPostingEntry(JobDTO skillDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View jobPostingEntry = inflater
                .inflate(R.layout.job_posting_entry, viewJobPostingsContainer, false);

        TextView title = jobPostingEntry.findViewById(R.id.jobTitle);
        TextView company = jobPostingEntry.findViewById(R.id.company);
        TextView workplaceType = jobPostingEntry.findViewById(R.id.workplaceType);
        TextView jobLocation = jobPostingEntry.findViewById(R.id.jobLocation);
        TextView employmentType = jobPostingEntry.findViewById(R.id.employmentType);

        title.setText(skillDTO.getJobTitle());
        company.setText(skillDTO.getCompany());
        workplaceType.setText(skillDTO.getWorkplaceType().toString());
        jobLocation.setText(skillDTO.getJobLocation());
        employmentType.setText(skillDTO.getEmploymentType().toString());

        Button deleteButton = jobPostingEntry.findViewById(R.id.jobPostingButton);
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(listener -> {
            viewJobPostingsContainer.removeView(jobPostingEntry);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }
}