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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NetworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkFragment extends Fragment {

    LinearLayout networkList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetworkFragment newInstance(String param1, String param2) {
        NetworkFragment fragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
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
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        networkList = requireView().findViewById(R.id.network_list);

        EditText searchBar = getActivity().findViewById(R.id.searchBar);
        searchBar.setHint("Search users...");

        UserDtoResultLiveData.getInstance().observe(getActivity(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.getUserByEmail(userDTO.getEmail()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        List<EnlargedUserDTO> connections = response.body().getConnections();
                        if (connections.isEmpty()){
                            TextView noConnectionsTextView = new TextView(getActivity());
                            noConnectionsTextView.setText("You have no connections. \n");
                            noConnectionsTextView.setTextSize(20); // Set desired text size
                            noConnectionsTextView.setTextColor(Color.BLACK);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(300, 300, 16, 16);
                            noConnectionsTextView.setLayoutParams(params);

                            networkList.addView(noConnectionsTextView);

                            TextView addConnectionsTextView = new TextView(getActivity());
                            addConnectionsTextView.setText("Add some through the search bar!");
                            addConnectionsTextView.setTextSize(20);
                            addConnectionsTextView.setTextColor(Color.BLACK);

                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params2.setMargins(200, 5, 16, 16);
                            addConnectionsTextView.setLayoutParams(params2);

                            networkList.addView(addConnectionsTextView);
                        }

                        for (EnlargedUserDTO connection : connections){
                            addEntryToList(connection);
                        }
                    }else{
                        Toast.makeText(getActivity(), "user fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("user fetch fail: " , t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "user fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void addEntryToList(EnlargedUserDTO connection) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View networkListEntry = inflater
                .inflate(R.layout.network_list_entry_template, networkList, false);

        TextView fullName = networkListEntry.findViewById(R.id.full_name);
        TextView position = networkListEntry.findViewById(R.id.position);
        TextView employer = networkListEntry.findViewById(R.id.employer);

        ImageView profilePic = networkListEntry.findViewById(R.id.user_profile_pic);
        String profilePicName = connection.getProfilePicture();
        List<SmallCustomFileDTO> files = connection.getFiles();
        Optional<SmallCustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        CustomFileService customFileService = retrofit.create(CustomFileService.class);

        if (profilePicture.isPresent()){
            customFileService.getCustomFileById(profilePicture.get().getId()).enqueue(new Callback<CustomFileDTO>() {
                @Override
                public void onResponse(Call<CustomFileDTO> call, Response<CustomFileDTO> response) {
                    if (response.isSuccessful()){
                        Bitmap bitmap = loadImageFromConnectionFile(response.body());
                        profilePic.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(getActivity(), "File fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CustomFileDTO> call, Throwable t) {
                    Log.d("file fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "File fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
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

        networkList.addView(networkListEntry);
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
        byte[] decompressedBytes = decompressData(bytes);
        return new ByteArrayInputStream(decompressedBytes);
    }

    // used to decompress the compressed data from the DB.
    private byte[] decompressData(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}