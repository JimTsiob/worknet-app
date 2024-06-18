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
import com.syrtsiob.worknet.model.ConnectionDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SkillDTO;

import java.util.List;

public class ViewJobPostingsFragment extends Fragment {

    LinearLayout viewJobPostingsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_job_postings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewJobPostingsContainer = requireView().findViewById(R.id.viewJobPostingsContainer);

        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
            List<ConnectionDTO> connections = userDTO.getConnections();
            List<SkillDTO> skills = userDTO.getSkills();

            if (connections.isEmpty() && skills.isEmpty()){
                TextView noJobPostsRecommendedTextView = new TextView(getActivity());
                noJobPostsRecommendedTextView.setText("No job posts can be recommended for now. \n");
                noJobPostsRecommendedTextView.setTextSize(20); // Set desired text size
                noJobPostsRecommendedTextView.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(200, 300, 16, 16);
                noJobPostsRecommendedTextView.setLayoutParams(params);

                viewJobPostingsContainer.addView(noJobPostsRecommendedTextView);

                TextView addConnectionsOrSkills = new TextView(getActivity());
                addConnectionsOrSkills.setText("Add some connections or skills!");
                addConnectionsOrSkills.setTextSize(20);
                addConnectionsOrSkills.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params2.setMargins(200, 5, 16, 16);
                addConnectionsOrSkills.setLayoutParams(params2);

                viewJobPostingsContainer.addView(addConnectionsOrSkills);
            }
        });

        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();
        AddJobPostingEntry();

    }

    // TODO this is for testing purposes -- remove
    private void AddJobPostingEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View jobPostingEntry = inflater
                .inflate(R.layout.job_posting_entry_lite, viewJobPostingsContainer, false);

        Button learnMoreButton = jobPostingEntry.findViewById(R.id.jobPostingButton);
        learnMoreButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), JobPostingDetails.class);
            startActivity(intent);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }

    private void AddJobPostingEntry(JobDTO jobDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View jobPostingEntry = inflater
                .inflate(R.layout.job_posting_entry_lite, viewJobPostingsContainer, false);

        TextView title = jobPostingEntry.findViewById(R.id.jobTitle);
        TextView company = jobPostingEntry.findViewById(R.id.company);

        title.setText(jobDTO.getJobTitle());
        company.setText(jobDTO.getCompany());

        Button learnMoreButton = jobPostingEntry.findViewById(R.id.jobPostingButton);
        learnMoreButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), JobPostingDetails.class);
            intent.putExtra(JobPostingDetails.SERIALIZABLE, jobDTO);
            startActivity(intent);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }
}