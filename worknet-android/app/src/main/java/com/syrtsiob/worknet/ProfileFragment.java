package com.syrtsiob.worknet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.syrtsiob.worknet.LiveData.ApplicantUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EducationDTO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 profileViewPager;
    ProfileViewPagerAdapter profileViewPagerAdapter;


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
        // and initialize connection data with null for proper functionality
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clear the ConnectionDTO when the fragment view is destroyed
        // so we can see the logged in user's profile
        ConnectionUserDtoResultLiveData.getInstance().setValue(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = requireView().findViewById(R.id.profileTabLayout);
        profileViewPager = requireView().findViewById(R.id.profileViewPager);
        profileViewPagerAdapter = new ProfileViewPagerAdapter(this);

        profileViewPager.setAdapter(profileViewPagerAdapter);

        // if user comes here to see an applicant show the applicant's profile.
        ApplicantUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), applicantDTO -> {
                    if (applicantDTO != null) {
                        ImageView profilePic = requireView().findViewById(R.id.profilePagePic);
                        String profilePicName = applicantDTO.getProfilePicture();
                        List<CustomFileDTO> files = applicantDTO.getFiles();
                        Optional<CustomFileDTO> profilePicture = files.stream()
                                .filter(file -> file.getFileName().equals(profilePicName))
                                .findFirst();
                        if (profilePicture.isPresent()) {
                            Bitmap bitmap = loadImageFromFile(profilePicture.get());
                            profilePic.setImageBitmap(bitmap);
                        }

                        TextView fullName = requireView().findViewById(R.id.fullNameProfile);
                        fullName.setText(applicantDTO.getFirstName() + " " + applicantDTO.getLastName());
                    }
                });

        // if user clicks on connection profile show connection profile elements.
        // Otherwise show own profile elements.
        ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
            if (connectionDTO != null){
                ImageView profilePic = requireView().findViewById(R.id.profilePagePic);
                String profilePicName = connectionDTO.getProfilePicture();
                List<CustomFileDTO> files = connectionDTO.getFiles();
                Optional<CustomFileDTO> profilePicture = files.stream()
                        .filter(file -> file.getFileName().equals(profilePicName))
                        .findFirst();
                if (profilePicture.isPresent()) {
                    Bitmap bitmap = loadImageFromFile(profilePicture.get());
                    profilePic.setImageBitmap(bitmap);
                }

                TextView fullName = requireView().findViewById(R.id.fullNameProfile);
                fullName.setText(connectionDTO.getFirstName() + " " + connectionDTO.getLastName());
            }else{
                UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                    if (userDTO != null){
                        ImageView profilePic = requireView().findViewById(R.id.profilePagePic);
                        String profilePicName = userDTO.getProfilePicture();
                        List<CustomFileDTO> files = userDTO.getFiles();
                        Optional<CustomFileDTO> profilePicture = files.stream()
                                .filter(file -> file.getFileName().equals(profilePicName))
                                .findFirst();
                        if (profilePicture.isPresent()) {
                            Bitmap bitmap = loadImageFromFile(profilePicture.get());
                            profilePic.setImageBitmap(bitmap);
                        }

                        TextView fullName = requireView().findViewById(R.id.fullNameProfile);
                        fullName.setText(userDTO.getFirstName() + " " + userDTO.getLastName());
                    }
                });
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profileViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        profileViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    // method that returns images from the database.
    private Bitmap loadImageFromFile(CustomFileDTO file) {
        InputStream inputStream = decodeBase64ToInputStream(file.getFileContent());
        return BitmapFactory.decodeStream(inputStream);
    }

    // used to decode the image's base64 string from the db
    private InputStream decodeBase64ToInputStream(String base64Data) {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        return new ByteArrayInputStream(bytes);
    }


}