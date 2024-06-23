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

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewJobPostingsFragment extends Fragment {

    LinearLayout viewJobPostingsContainer;

    public static ViewJobPostingsFragment newInstance() {
        return new ViewJobPostingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_job_postings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getRecommendedJobs();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewJobPostingsContainer = requireView().findViewById(R.id.viewJobPostingsContainer);

    }

    private void AddJobPostingEntry(JobDTO jobDTO, Long userId) {
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
            intent.putExtra(JobPostingDetails.USER_ID.toString(), userId);
            startActivity(intent);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }

    private void showEmptyJobRecommendations(){
        TextView noJobPostsRecommendedTextView = new TextView(getActivity());
        noJobPostsRecommendedTextView.setText("No job posts can be recommended for now. \n");
        noJobPostsRecommendedTextView.setTextSize(20); // Set desired text size
        noJobPostsRecommendedTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(75, 300, 16, 16);
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

    private void getRecommendedJobs(){
        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {


            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        List<EnlargedUserDTO> connections = response.body().getConnections();
                        List<SkillDTO> skills = response.body().getSkills();

                        if (connections.isEmpty() && skills.isEmpty()){
                            showEmptyJobRecommendations();
                            return;
                        }

                        userService.recommendJobs(userDTO.getId()).enqueue(new Callback<List<JobDTO>>() {
                            @Override
                            public void onResponse(Call<List<JobDTO>> call, Response<List<JobDTO>> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().isEmpty()){
                                        showEmptyJobRecommendations();
                                        return;
                                    }

                                    // removes duplicates
                                    viewJobPostingsContainer.removeAllViews();

                                    for (JobDTO job: response.body()){
                                        AddJobPostingEntry(job, userDTO.getId());
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "jobs recommendation failed! Check the format.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<JobDTO>> call, Throwable t) {
                                Log.e("fail: ", t.getLocalizedMessage());
                                // Handle the error
                                Toast.makeText(getActivity(), "jobs recommendation failed! Server failure.", Toast.LENGTH_LONG).show();
                            }
                        });


                    }else{
                        Toast.makeText(getActivity(), "jobs recommendation failed! Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.e("fail: ", t.getLocalizedMessage());
                    // Handle the error
                    Toast.makeText(getActivity(), "jobs recommendation failed! Server failure.", Toast.LENGTH_LONG).show();
                }
            });

        });
    }
}