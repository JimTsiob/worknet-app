package com.syrtsiob.worknet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;

public class MessagesFragment extends Fragment {

    LinearLayout chatsList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
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
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatsList = requireView().findViewById(R.id.notificationContainer);

        AddChatEntry();
        AddChatEntry();
        AddChatEntry();
    }

    // TODO this is for testing purposes -- remove
    private void AddChatEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View chatEntry = inflater
                .inflate(R.layout.chat_list_entry, chatsList, false);

        chatEntry.setOnClickListener(listener -> {
            // TODO new chat activity
        });

        chatsList.addView(chatEntry);
    }

    private void AddChatEntry(SmallUserDTO smallUserDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View chatEntry = inflater
                .inflate(R.layout.chat_list_entry, chatsList, false);

        ImageView picture = chatEntry.findViewById(R.id.chatImage);
        TextView userName = chatEntry.findViewById(R.id.chatUserName);

        // TODO update change picture or remove it from notification structure
        userName.setText(smallUserDTO.getFirstName() + " " + smallUserDTO.getLastName());

        chatEntry.setOnClickListener(listener -> {
            // TODO new chat activity
        });

        chatsList.addView(chatEntry);
    }
}