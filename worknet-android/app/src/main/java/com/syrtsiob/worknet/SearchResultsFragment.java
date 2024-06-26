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

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {

    static final String DEFAULT_MODE = "default_mode";
    static final String HOME_FRAG_MODE = "home_frag_mode";

    private static final String RESULTS_MODE = "results_mode";
    private static final String SEARCH_INPUT = "search_input";

    private String resultsMode;
    private String searchInput;

    LinearLayout resultsContainer;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use to create a new instance of search results
     *
     * @param resultsMode The results mode depending on the context. Use SearchResultsFragment's static finals
     * @param searchInput The user's search input.
     * @return A new instance of fragment SearchResultsFragment.
     */
    public static SearchResultsFragment newInstance(String resultsMode, String searchInput) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(RESULTS_MODE, resultsMode);
        args.putString(SEARCH_INPUT, searchInput);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resultsMode = getArguments().getString(RESULTS_MODE);
            searchInput = getArguments().getString(SEARCH_INPUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultsContainer = requireView().findViewById(R.id.resultsContainer);

        switch (resultsMode) {
            case HOME_FRAG_MODE:
                HandleHomeFragMode(searchInput);
                break;
            case DEFAULT_MODE:
                HandleDefaultMode(searchInput);
                break;
            default:
                HandleUnknownMode();
        }
    }

    private void HandleDefaultMode(String searchInput) {
        // TODO implement user search
        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        UserService userService = retrofit.create(UserService.class);

        userService.searchUser(searchInput).enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful()){
                    if (response.body().isEmpty()){
                        ShowNoResults();
                    }

                    for (UserDTO user: response.body()){
                        EnlargedUserDTO convertedUser = new EnlargedUserDTO();
                        convertedUser.setEducations(user.getEducations());
                        convertedUser.setSkills(user.getSkills());
                        convertedUser.setWorkExperiences(user.getWorkExperiences());
                        convertedUser.setProfilePicture(user.getProfilePicture());
                        convertedUser.setId(user.getId());
                        convertedUser.setFirstName(user.getFirstName());
                        convertedUser.setLastName(user.getLastName());
                        convertedUser.setFiles(user.getFiles());

                        addEntryToList(convertedUser);
                    }

                }else{
                    Toast.makeText(getActivity(), "User search failed. Check the format.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Log.d("user search fail", t.getLocalizedMessage());
                Toast.makeText(getActivity(), "User search failed. Server failure.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void HandleHomeFragMode(String searchInput) {
        // TODO implement post search
        // if (results != null)

        // else if (results == null)
        ShowNoResults();
    }

    private void HandleUnknownMode() {
        Toast.makeText(getActivity(),
                "Error when loading search results", Toast.LENGTH_LONG).show();
    }

    private void ShowNoResults() {
        TextView errorMessage = new TextView(getActivity());
        errorMessage.setText(R.string.no_results);
        errorMessage.setTextSize(20);
        errorMessage.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 64, 0, 0);
        errorMessage.setLayoutParams(params);
        errorMessage.setGravity(Gravity.CENTER);

        resultsContainer.addView(errorMessage);
    }

    private void addEntryToList(EnlargedUserDTO connection) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View networkListEntry = inflater
                .inflate(R.layout.network_list_entry_template, resultsContainer, false);

        TextView fullName = networkListEntry.findViewById(R.id.full_name);
        TextView position = networkListEntry.findViewById(R.id.position);
        TextView employer = networkListEntry.findViewById(R.id.employer);

        ImageView profilePic = networkListEntry.findViewById(R.id.user_profile_pic);
        String profilePicName = connection.getProfilePicture();
        List<CustomFileDTO> files = connection.getFiles();
        Optional<CustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        if (profilePicture.isPresent()){
            Bitmap bitmap = loadImageFromConnectionFile(profilePicture.get());
            profilePic.setImageBitmap(bitmap);
        }

        fullName.setText(connection.getFirstName() + " " + connection.getLastName());
        List<WorkExperienceDTO> workExperiences = connection.getWorkExperiences();

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

        Button goToProfileButton = networkListEntry.findViewById(R.id.goToProfileButton);

        goToProfileButton.setOnClickListener(listener -> {
            // add connection and once user leaves profile page reset to proper user.
            ConnectionUserDtoResultLiveData.getInstance().setValue(connection);
            replaceFragment(ProfileFragment.newInstance());
        });

        resultsContainer.addView(networkListEntry);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }

    // method that returns images from the db.
    private Bitmap loadImageFromConnectionFile(CustomFileDTO file) {
        InputStream inputStream = decodeBase64ToInputStream(file.getFileContent());
        return BitmapFactory.decodeStream(inputStream);
    }

    // used to decode the image's base64 string from the db.
    private InputStream decodeBase64ToInputStream(String base64Data) {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        return new ByteArrayInputStream(bytes);
    }
}