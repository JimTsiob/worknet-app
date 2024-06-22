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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SkillDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
            List<NotificationDTO> notifications = userDTO.getReceivedNotifications();

            if (!notifications.isEmpty()){
                for (NotificationDTO notification: notifications){
                    AddNotification(notification);
                }
            }else{
                showEmptyNotifications();
            }

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
        List<CustomFileDTO> files = notificationDTO.getSender().getFiles();
        Optional<CustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        if (profilePicture.isPresent()){
            Bitmap bitmap = loadImageFromConnectionFile(profilePicture.get());
            picture.setImageBitmap(bitmap);
        }

        TextView text = notificationEntry.findViewById(R.id.notificationText);
        Button leftButton = notificationEntry.findViewById(R.id.notificationButton1);
        Button rightButton = notificationEntry.findViewById(R.id.notificationButton2);

        text.setText(notificationDTO.getText());

        NotificationType notificationType = notificationDTO.getNotificationType();
        switch (notificationType) {
            case CONNECTION:
                // TODO implement
                break;
            case APPLY_TO_JOB_POST:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 80, 0);

                leftButton.setLayoutParams(params);
                leftButton.setText("Go to job posting");

                leftButton.setOnClickListener(listener -> {
                    replaceFragment(ViewMyJobPostingsFragment.newInstance());
                });

                rightButton.setVisibility(View.GONE);
                break;
            case LIKE_POST:
                // TODO implement
                break;
            case MESSAGE:
                // TODO implement
                break;
            case COMMENT:
                // TODO implement
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
        return new ByteArrayInputStream(bytes);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}