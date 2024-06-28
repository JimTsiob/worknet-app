package com.syrtsiob.worknet;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SkillDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.EducationService;
import com.syrtsiob.worknet.services.NotificationService;
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

public class NotificationsFragment extends Fragment {

    LinearLayout notificationContainer;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationContainer = requireView().findViewById(R.id.notificationContainer);

        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.getUserById(userDTO.getId()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        List<NotificationDTO> notifications = response.body().getReceivedNotifications();

                        if (!notifications.isEmpty()){
                            for (NotificationDTO notification: notifications){
                                AddNotification(notification);
                            }
                        }else{
                            showEmptyNotifications();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Notifications fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("notifs failure: ", t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "Notifications fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });



        });
    }

    private void showEmptyNotifications(){
        TextView noNotificationsTextView = new TextView(getActivity());
        noNotificationsTextView.setText("There are no notifications currently for you.");
        noNotificationsTextView.setTextSize(20); // Set desired text size
        noNotificationsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 300, 16, 16);
        noNotificationsTextView.setLayoutParams(params);

        notificationContainer.addView(noNotificationsTextView);
    }

    private void AddNotification(NotificationDTO notificationDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View notificationEntry = inflater
                .inflate(R.layout.notification_entry, notificationContainer, false);

        ImageView picture = notificationEntry.findViewById(R.id.notificationPicture);

        String profilePicName = notificationDTO.getSender().getProfilePicture();
        List<SmallCustomFileDTO> files = notificationDTO.getSender().getFiles();
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
                        picture.setImageBitmap(bitmap);
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

        TextView text = notificationEntry.findViewById(R.id.notificationText);
        Button leftButton = notificationEntry.findViewById(R.id.notificationButton1);
        Button rightButton = notificationEntry.findViewById(R.id.notificationButton2);

        text.setText(notificationDTO.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        NotificationType notificationType = notificationDTO.getNotificationType();
        switch (notificationType) {
            case CONNECTION:

                leftButton.setText("Accept");
                rightButton.setText("Decline");

                NotificationService notificationService = retrofit.create(NotificationService.class);

                leftButton.setOnClickListener(listener -> {

                    UserService userService = retrofit.create(UserService.class);

                    userService.addConnection(notificationDTO.getSender().getId(), notificationDTO.getReceiver().getId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()){
                                replaceFragment(HomeFragment.newInstance());
                                Toast.makeText(getActivity(), "Connection with user " + notificationDTO.getSender().getFirstName()
                                        + " " + notificationDTO.getSender().getLastName() + " added successfully.", Toast.LENGTH_LONG).show();

                                notificationService.deleteNotification(notificationDTO.getId()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()){
                                            notificationContainer.removeView(notificationEntry);
                                        }else{
                                            Toast.makeText(getActivity(), "Notification failed. Check the format.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("notification failure: ", t.getLocalizedMessage());
                                        Toast.makeText(getActivity(), "Notification failed. Server failure.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(), "Connection failed. Check the format.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("connection failure: ", t.getLocalizedMessage());
                            Toast.makeText(getActivity(), "Connection failed. Server failure.", Toast.LENGTH_LONG).show();
                        }
                    });
                });

                rightButton.setOnClickListener(listener -> {
                    notificationContainer.removeView(notificationEntry);
                    notificationService.deleteNotification(notificationDTO.getId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()){
                                // do nothing
                            }else{
                                Toast.makeText(getActivity(), "Notification failed. Check the format.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("notification failure: ", t.getLocalizedMessage());
                            Toast.makeText(getActivity(), "Notification failed. Server failure.", Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(getActivity(), "Connection declined.", Toast.LENGTH_LONG).show();
                });

                break;
            case APPLY_TO_JOB_POST:

                params.setMargins(0, 0, 80, 0);

                leftButton.setLayoutParams(params);
                leftButton.setText("Go to job posting");

                leftButton.setOnClickListener(listener -> {
                    replaceFragment(ViewMyJobPostingsFragment.newInstance());
                });

                rightButton.setVisibility(View.GONE);
                break;
            case LIKE_POST:
                params.setMargins(0, 0, 55, 0);

                leftButton.setLayoutParams(params);
                leftButton.setText("Go to post");

                leftButton.setOnClickListener(listener -> {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(PostActivity.POST_DTO_ID, notificationDTO.getPost().getId());
                    intent.putExtra(PostActivity.USER_ID, notificationDTO.getReceiver().getId());
                    startActivity(intent);
                });

                rightButton.setVisibility(View.GONE);

                break;
            case MESSAGE:
                params.setMargins(0, 0, 30, 0);

                leftButton.setLayoutParams(params);
                leftButton.setText("Go to messages");

                leftButton.setOnClickListener(listener -> {
                    replaceFragment(MessagesFragment.newInstance());
                });

                rightButton.setVisibility(View.GONE);
                break;
            case COMMENT:
                params.setMargins(0, 0, 55, 0);

                leftButton.setLayoutParams(params);
                leftButton.setText("Go to post");

                leftButton.setOnClickListener(listener -> {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(PostActivity.POST_DTO_ID, notificationDTO.getPost().getId());
                    intent.putExtra(PostActivity.USER_ID, notificationDTO.getReceiver().getId());
                    startActivity(intent);
                });

                rightButton.setVisibility(View.GONE);
                break;
        }

        notificationContainer.addView(notificationEntry);
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}