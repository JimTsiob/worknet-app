package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SkillDTO;

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

        AddNotification();
        AddNotification();
        AddNotification();
    }

    // TODO this is for testing purposes -- remove
    private void AddNotification() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View notificationEntry = inflater
                .inflate(R.layout.notification_entry, notificationContainer, false);

        notificationEntry.setOnClickListener(listener -> {
            // TODO implement switch based on context
        });

        notificationContainer.addView(notificationEntry);
    }

    private void AddNotification(NotificationDTO notificationDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View notificationEntry = inflater
                .inflate(R.layout.notification_entry, notificationContainer, false);

        ImageView picture = notificationEntry.findViewById(R.id.notificationPicture);
        TextView text = notificationEntry.findViewById(R.id.notificationText);

        // TODO update change picture or remove it from notification structure
        text.setText(notificationDTO.getText());

        notificationEntry.setOnClickListener(listener -> {
            NotificationType notificationType = notificationDTO.getNotificationType();
            switch (notificationType) {
                case CONNECTION:
                    // TODO implement
                    break;
                case APPLY_TO_JOB_POST:
                    // TODO implement
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
        });

        notificationContainer.addView(notificationEntry);
    }
}