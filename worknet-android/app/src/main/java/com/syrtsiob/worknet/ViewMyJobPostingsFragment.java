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
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.JobService;
import com.syrtsiob.worknet.services.UserService;

import java.util.List;
import java.util.StringJoiner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewMyJobPostingsFragment extends Fragment {

    Button createJobPostingButton;
    LinearLayout viewJobPostingsContainer;

    private int requiredItems = 2;

    private int itemsDisplayed = 0; // these two variables are used to prevent duplicate service calls.

    private int jobPostListSize = 0; // used for showing empty text after all job posts have been deleted.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_my_job_postings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (itemsDisplayed >= requiredItems){
            itemsDisplayed = 0;
            fetchData(); // had to adapt code here, this seems to do the same job for instant updating.
            return;
        }

        fetchData();

        itemsDisplayed = 0; // reset in order to have realistic updating in the page.
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewJobPostingsContainer = requireView().findViewById(R.id.my_job_posting_list);

        createJobPostingButton = requireView().findViewById(R.id.createJobPost);
        createJobPostingButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditJobPost.class);
            intent.putExtra(AddEditJobPost.ACTIVITY_MODE, AddEditJobPost.ADD_MODE);
            startActivity(intent);
        });
    }

    public void fetchData(){
        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            viewJobPostingsContainer = requireView().findViewById(R.id.my_job_posting_list);
            viewJobPostingsContainer.removeAllViews(); // clear before showing new ones. Removes duplicates

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        List<JobDTO> jobPosts = response.body().getJobs();

                        requiredItems = jobPosts.size();
                        jobPostListSize = jobPosts.size();

                        if (jobPosts.size() == 0){
                            requiredItems = 1;
                        }

                        if (jobPosts.isEmpty()){

                            if (itemsDisplayed >= requiredItems){ // removes duplicates
                                return;
                            }

                            showEmptyJobPosts();
                        }else{
                            for (JobDTO jobPost: jobPosts){
                                if (itemsDisplayed >= requiredItems){
                                    return;
                                }
                                AddJobPostingEntry(jobPost);
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

    private void showEmptyJobPosts(){
        TextView noJobPostsTextView = new TextView(getActivity());
        noJobPostsTextView.setText("You haven't created any job posts yet. \n");
        noJobPostsTextView.setTextSize(20); // Set desired text size
        noJobPostsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(140, 300, 16, 16);
        noJobPostsTextView.setLayoutParams(params);

        viewJobPostingsContainer.addView(noJobPostsTextView);

        TextView addJobPostsTextView = new TextView(getActivity());
        addJobPostsTextView.setText("Add some with the button above!");
        addJobPostsTextView.setTextSize(20);
        addJobPostsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(200, 5, 16, 16);
        addJobPostsTextView.setLayoutParams(params2);

        itemsDisplayed += 1;

        viewJobPostingsContainer.addView(addJobPostsTextView);
    }

    private void AddJobPostingEntry(JobDTO jobDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View jobPostingEntry = inflater
                .inflate(R.layout.job_posting_entry, viewJobPostingsContainer, false);

        TextView title = jobPostingEntry.findViewById(R.id.jobTitle);
        TextView company = jobPostingEntry.findViewById(R.id.company);
        TextView workplaceType = jobPostingEntry.findViewById(R.id.workplaceType);
        TextView jobLocation = jobPostingEntry.findViewById(R.id.jobLocation);
        TextView employmentType = jobPostingEntry.findViewById(R.id.employmentType);
        TextView skills = jobPostingEntry.findViewById(R.id.job_skills);

        title.setText(jobDTO.getJobTitle());
        company.setText(jobDTO.getCompany());
        workplaceType.setText(jobDTO.getWorkplaceType().toString());
        jobLocation.setText(jobDTO.getJobLocation());

        if (jobDTO.getEmploymentType().toString().equals("FULL_TIME")){
            employmentType.setText("Full time position");
        }else if (jobDTO.getEmploymentType().toString().equals("PART_TIME")){
            employmentType.setText("Part time position");
        }else{
            employmentType.setText("Contract position");
        }

        if (jobDTO.getWorkplaceType().toString().equals("ON_SITE")){
            workplaceType.setText("On site");
        }else if (jobDTO.getWorkplaceType().toString().equals("REMOTE")){
            workplaceType.setText("Remote");
        }else{
            workplaceType.setText("Hybrid");
        }

        // show each skill separated by a comma and space, apart from the last one
        StringJoiner skillset = new StringJoiner(", ");
        for (SkillDTO skill : jobDTO.getSkills()) {
            skillset.add(skill.getName());
        }

        skills.setText("Skills: " + skillset.toString());



        Button editButton = jobPostingEntry.findViewById(R.id.editJobPostingButton);
        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditJobPost.class);
            intent.putExtra(AddEditJobPost.ACTIVITY_MODE, AddEditJobPost.EDIT_MODE);
            intent.putExtra(AddEditJobPost.SERIALIZABLE, jobDTO);
            intent.putExtra(AddEditJobPost.JOB_POST_ID.toString(), jobDTO.getId());
            startActivity(intent);
        });

        Button deleteButton = jobPostingEntry.findViewById(R.id.deleteJobPostingButton);
        deleteButton.setOnClickListener(listener -> {
            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

                Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                JobService jobService = retrofit.create(JobService.class);

                jobService.deleteJob(jobDTO.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            jobPostListSize -= 1;
                            if (jobPostListSize == 0){ // show empty text if list is empty (dynamically).
                                showEmptyJobPosts();
                            }
                            Toast.makeText(getActivity(), "deleted job post successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "job post deletion failed. Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Handle the error
                        Log.e("fail: ", t.getLocalizedMessage());
                    }
                });
            });

            viewJobPostingsContainer.removeView(jobPostingEntry);
        });

        viewJobPostingsContainer.addView(jobPostingEntry);
    }
}