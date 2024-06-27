package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.NonConnectedUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.NotificationService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 profileViewPager;
    ProfileViewPagerAdapter profileViewPagerAdapter;

    Button connectButton;

    Button sendMessageButton;


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
        // Clear the connected user and the non-connected user when the fragment view is destroyed
        // so we can see the logged in user's profile
        ConnectionUserDtoResultLiveData.getInstance().setValue(null);
        NonConnectedUserDtoResultLiveData.getInstance().setValue(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = requireView().findViewById(R.id.profileTabLayout);
        profileViewPager = requireView().findViewById(R.id.profileViewPager);

        connectButton = requireView().findViewById(R.id.connectButton);
        sendMessageButton = requireView().findViewById(R.id.sendMessage);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(this);

        profileViewPager.setAdapter(profileViewPagerAdapter);

        // if user comes here to see a non-connected user show the non-connected user's profile.
        NonConnectedUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), nonConnectedUserDTO -> {
                    if (nonConnectedUserDTO != null) {
                        ImageView profilePic = requireView().findViewById(R.id.profilePagePic);
                        String profilePicName = nonConnectedUserDTO.getProfilePicture();
                        List<CustomFileDTO> files = nonConnectedUserDTO.getFiles();
                        Optional<CustomFileDTO> profilePicture = files.stream()
                                .filter(file -> file.getFileName().equals(profilePicName))
                                .findFirst();
                        if (profilePicture.isPresent()) {
                            Bitmap bitmap = loadImageFromFile(profilePicture.get());
                            profilePic.setImageBitmap(bitmap);
                        }

                        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                        UserService userService = retrofit.create(UserService.class);

                        // disable button if notification has already been sent before.
                        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                            userService.getUserById(userDTO.getId()).enqueue(new Callback<UserDTO>() {
                                @Override
                                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                    if (response.isSuccessful()){
                                        List<NotificationDTO> receivedNotifs = response.body().getReceivedNotifications();
                                        List<NotificationDTO> sentNotifs = response.body().getSentNotifications();

                                        List<NotificationDTO> allNotifs = new ArrayList<>(receivedNotifs);
                                        allNotifs.addAll(sentNotifs);

                                        for (NotificationDTO notification: allNotifs){
                                            if (hasConnectionNotification(notification, userDTO, nonConnectedUserDTO)){
                                                connectButton.setEnabled(false);
                                            }
                                        }

                                    }else{
                                        Toast.makeText(getActivity(), "User fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserDTO> call, Throwable t) {
                                    Toast.makeText(getActivity(), "User fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                                }
                            });
                        });

                        // send notification for connection
                        connectButton.setOnClickListener(listener -> {
                            NotificationService notificationService = retrofit.create(NotificationService.class);

                            NotificationDTO notificationDTO = new NotificationDTO();
                            NotificationType notificationType = NotificationType.valueOf("CONNECTION");
                            notificationDTO.setNotificationType(notificationType);

                            SmallUserDTO smallUserDTO = new SmallUserDTO();
                            smallUserDTO.setLastName(nonConnectedUserDTO.getLastName());
                            smallUserDTO.setFirstName(nonConnectedUserDTO.getFirstName());
                            smallUserDTO.setId(nonConnectedUserDTO.getId());

                            notificationDTO.setReceiver(smallUserDTO);


                            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                                EnlargedUserDTO enlargedUserDTO = new EnlargedUserDTO();
                                enlargedUserDTO.setEmail(userDTO.getEmail());
                                enlargedUserDTO.setId(userDTO.getId());
                                enlargedUserDTO.setSkills(userDTO.getSkills());
                                enlargedUserDTO.setProfilePicture(userDTO.getProfilePicture());
                                enlargedUserDTO.setFiles(userDTO.getFiles());
                                enlargedUserDTO.setWorkExperiences(userDTO.getWorkExperiences());
                                enlargedUserDTO.setEducations(userDTO.getEducations());
                                enlargedUserDTO.setFirstName(userDTO.getFirstName());
                                enlargedUserDTO.setLastName(userDTO.getLastName());

                                notificationDTO.setText(enlargedUserDTO.getFirstName() + " " + enlargedUserDTO.getLastName() +
                                        " wants to connect with you.");

                                notificationDTO.setSender(enlargedUserDTO);

                                notificationService.addNotification(notificationDTO).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()){
                                            Toast.makeText(getActivity(), "Pending connection sent successfully.", Toast.LENGTH_LONG).show();
                                            connectButton.setEnabled(false); // disable button so as not to spam user with notifs.
                                        }else{
                                            Toast.makeText(getActivity(), "Pending connection failed. Check the format", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("notification failure: ", t.getLocalizedMessage());
                                        Toast.makeText(getActivity(), "notification failed. Server failure", Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        });

                        sendMessageButton.setOnClickListener(listener -> {
                            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                                if (userDTO != null){
                                    EnlargedUserDTO loggedInUser = new EnlargedUserDTO();
                                    loggedInUser.setEducations(userDTO.getEducations());
                                    loggedInUser.setEmail(userDTO.getEmail());
                                    loggedInUser.setId(userDTO.getId());
                                    loggedInUser.setFiles(userDTO.getFiles());
                                    loggedInUser.setFirstName(userDTO.getFirstName());
                                    loggedInUser.setLastName(userDTO.getLastName());
                                    loggedInUser.setProfilePicture(userDTO.getProfilePicture());
                                    loggedInUser.setSkills(userDTO.getSkills());
                                    loggedInUser.setWorkExperiences(userDTO.getWorkExperiences());

                                    //  go to chat with this connection
                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtra(Chat.SERIALIZABLE_LOGGED_IN_USER, loggedInUser);
                                    intent.putExtra(Chat.SERIALIZABLE_OTHER_USER, nonConnectedUserDTO);
                                    startActivity(intent);
                                }
                            });
                        });

                        TextView fullName = requireView().findViewById(R.id.fullNameProfile);
                        fullName.setText(nonConnectedUserDTO.getFirstName() + " " + nonConnectedUserDTO.getLastName());
                    }else{
                        // if user clicks on connection profile show connection profile elements.
                        // Otherwise show own profile elements.
                        ConnectionUserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), connectionDTO -> {
                            if (connectionDTO != null){

                                connectButton.setVisibility(View.GONE);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 55, 0, 0);
                                sendMessageButton.setLayoutParams(params);

                                sendMessageButton.setOnClickListener(listener -> {
                                    UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                                        if (userDTO != null){
                                            EnlargedUserDTO loggedInUser = new EnlargedUserDTO();
                                            loggedInUser.setEducations(userDTO.getEducations());
                                            loggedInUser.setEmail(userDTO.getEmail());
                                            loggedInUser.setId(userDTO.getId());
                                            loggedInUser.setFiles(userDTO.getFiles());
                                            loggedInUser.setFirstName(userDTO.getFirstName());
                                            loggedInUser.setLastName(userDTO.getLastName());
                                            loggedInUser.setProfilePicture(userDTO.getProfilePicture());
                                            loggedInUser.setSkills(userDTO.getSkills());
                                            loggedInUser.setWorkExperiences(userDTO.getWorkExperiences());

                                            //  go to chat with this connection
                                            Intent intent = new Intent(getActivity(), Chat.class);
                                            intent.putExtra(Chat.SERIALIZABLE_LOGGED_IN_USER, loggedInUser);
                                            intent.putExtra(Chat.SERIALIZABLE_OTHER_USER, connectionDTO);
                                            startActivity(intent);
                                        }
                                    });
                                });

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
                                        connectButton.setVisibility(View.GONE);
                                        sendMessageButton.setVisibility(View.GONE);

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

    // method that returns if a notification for connection has been sent to disable the connect button.
    private boolean hasConnectionNotification(NotificationDTO notificationDTO, UserDTO loggedInUser, EnlargedUserDTO nonConnectedUser){
        if ((notificationDTO.getNotificationType().toString().equals("CONNECTION")
                && (Objects.equals(notificationDTO.getSender().getId(), loggedInUser.getId())
                && (Objects.equals(notificationDTO.getReceiver().getId(), nonConnectedUser.getId())))) ||
                (notificationDTO.getNotificationType().toString().equals("CONNECTION")
                && (Objects.equals(notificationDTO.getSender().getId(), nonConnectedUser.getId()))
                && (Objects.equals(notificationDTO.getReceiver().getId(), loggedInUser.getId())))){
                return true;
        }

        return false;
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