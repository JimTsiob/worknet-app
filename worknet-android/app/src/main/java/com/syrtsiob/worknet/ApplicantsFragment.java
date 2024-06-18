package com.syrtsiob.worknet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.LiveData.ApplicantUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.ConnectionDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApplicantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApplicantsFragment extends Fragment {

    LinearLayout applicantsList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ApplicantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApplicantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApplicantsFragment newInstance(String param1, String param2) {
        ApplicantsFragment fragment = new ApplicantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ApplicantsFragment newInstance() {
        return new ApplicantsFragment();
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
        return inflater.inflate(R.layout.fragment_applicants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applicantsList = requireView().findViewById(R.id.applicant_list);

        UserDtoResultLiveData.getInstance().observe(getActivity(), userDTO -> {
            List<JobDTO> jobs = userDTO.getJobs();
            List<SmallUserDTO> applicants = new ArrayList<>();
            for (JobDTO job: jobs){
                applicants.addAll(job.getInterestedUsers());
            }

            if (applicants.isEmpty()){
                TextView noApplicantsTextView = new TextView(getActivity());
                noApplicantsTextView.setText("There are no applicants. \n");
                noApplicantsTextView.setTextSize(20); // Set desired text size
                noApplicantsTextView.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(300, 300, 16, 16);
                noApplicantsTextView.setLayoutParams(params);

                applicantsList.addView(noApplicantsTextView);
            }


            for (SmallUserDTO applicant : applicants){
                addEntryToList(applicant);
            }
        });


    }

    // TODO replace with applicants?
    private void addEntryToList(SmallUserDTO applicant) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View applicantsListEntry = inflater
                .inflate(R.layout.network_list_entry_template, applicantsList, false);

        TextView fullName = applicantsListEntry.findViewById(R.id.full_name);
        TextView position = applicantsListEntry.findViewById(R.id.position);
        TextView employer = applicantsListEntry.findViewById(R.id.employer);

        ImageView profilePic = applicantsListEntry.findViewById(R.id.user_profile_pic);
        String profilePicName = applicant.getProfilePicture();
        List<CustomFileDTO> files = applicant.getFiles();
        Optional<CustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        if (profilePicture.isPresent()){
            Bitmap bitmap = loadImageFromConnectionFile(profilePicture.get().getFileName());
            profilePic.setImageBitmap(bitmap);
        }

        fullName.setText(applicant.getFirstName() + " " + applicant.getLastName());
        List<WorkExperienceDTO> workExperiences = applicant.getWorkExperiences();

        String positionText =  workExperiences.stream()
                .filter(WorkExperienceDTO::getCurrentlyWorking)
                .map(WorkExperienceDTO::getTitle)
                .findFirst()
                .orElse(null);

        position.setText(positionText);

        String employerText =  workExperiences.stream()
                .filter(WorkExperienceDTO::getCurrentlyWorking)
                .map(WorkExperienceDTO::getCompanyName)
                .findFirst()
                .orElse(null);

        employer.setText(employerText);

        Button goToProfileButton = applicantsListEntry.findViewById(R.id.goToProfileButton);

        goToProfileButton.setOnClickListener(listener -> {
            // add connection and once user leaves profile page reset to proper user.
            ApplicantUserDtoResultLiveData.getInstance().setValue(applicant);
            replaceFragment(ProfileFragment.newInstance());
        });

        applicantsList.addView(applicantsListEntry);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }

    // method that returns images from the phone's sd card.
    private Bitmap loadImageFromConnectionFile(String fileName) {
        File imgFile = new File(getActivity().getFilesDir(), "FileStorage/images/" + fileName);

        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

        return null;
    }
}