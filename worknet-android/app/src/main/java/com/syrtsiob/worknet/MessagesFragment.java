package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.MessageDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.MessageService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MessagesFragment extends Fragment {

    LinearLayout chatsList;

    int timesServiceCalled = 0; // used for preventing duplicate entries


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

        chatsList = requireView().findViewById(R.id.chatsList);

        EditText searchBar = getActivity().findViewById(R.id.searchBar);
        searchBar.setHint("Search users...");

        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            // Call server to refresh the page with new messages dynamically afterwards.

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.getUserById(userDTO.getId()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){
                        // do not allow duplicate service calls due to live data.
                        if (timesServiceCalled == 1){
                            return;
                        }

                        List<MessageDTO> receivedMessages = response.body().getReceivedMessages();
                        List<MessageDTO> sentMessages = response.body().getSentMessages();

                        if (receivedMessages.isEmpty() && sentMessages.isEmpty()){
                            showEmptyMessages();
                        }

                        // users who sent or received messages from the examined user.
                        List<EnlargedUserDTO> users = new ArrayList<>();

                        for (MessageDTO message: receivedMessages){
                            users.add(message.getSender());
                        }

                        for (MessageDTO message: sentMessages){
                            users.add(message.getReceiver());
                        }

                        // DTO used for proper chat functionality later on
                        EnlargedUserDTO loggedInUser = new EnlargedUserDTO();
                        loggedInUser.setEducations(userDTO.getEducations());
                        loggedInUser.setFiles(userDTO.getFiles());
                        loggedInUser.setEmail(userDTO.getEmail());
                        loggedInUser.setId(userDTO.getId());
                        loggedInUser.setLastName(userDTO.getLastName());
                        loggedInUser.setFirstName(userDTO.getFirstName());
                        loggedInUser.setSkills(userDTO.getSkills());
                        loggedInUser.setProfilePicture(userDTO.getProfilePicture());
                        loggedInUser.setWorkExperiences(userDTO.getWorkExperiences());

                        // Remove duplicate users

                        List<EnlargedUserDTO> uniqueUsers = removeDuplicates(users);

                        timesServiceCalled += 1;

                        // get all chat entries
                        for (EnlargedUserDTO user: uniqueUsers){
                            AddChatEntry(user, loggedInUser, userDTO);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {

                }
            });


        });
    }

    public static List<EnlargedUserDTO> removeDuplicates(List<EnlargedUserDTO> users) {
        Set<Long> seenIds = new HashSet<>();
        return users.stream()
                .filter(user -> seenIds.add(user.getId())) // Add to set if not already present
                .collect(Collectors.toList()); // Collect filtered users into a list
    }

    private void showEmptyMessages(){
        TextView noMessagesTextView = new TextView(getActivity());
        noMessagesTextView.setText("There are no messages currently for you.");
        noMessagesTextView.setTextSize(20); // Set desired text size
        noMessagesTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(120, 300, 16, 16);
        noMessagesTextView.setLayoutParams(params);

        chatsList.addView(noMessagesTextView);
    }

    private void AddChatEntry(EnlargedUserDTO enlargedUserDTO, EnlargedUserDTO loggedInUser, UserDTO loggedInUserDto) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View chatEntry = inflater
                .inflate(R.layout.chat_list_entry, chatsList, false);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(0, 50, 0, 0);
        chatEntry.setLayoutParams(params2);

        ImageView picture = chatEntry.findViewById(R.id.chatImage);
        TextView userName = chatEntry.findViewById(R.id.chatUserName);

        String profilePicName = enlargedUserDTO.getProfilePicture();
        List<SmallCustomFileDTO> files = enlargedUserDTO.getFiles();
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

        userName.setText(enlargedUserDTO.getFirstName() + " " + enlargedUserDTO.getLastName());

        chatEntry.setOnClickListener(listener -> {
            Intent intent = new Intent(getContext(), Chat.class);
            intent.putExtra(Chat.SERIALIZABLE_LOGGED_IN_USER, loggedInUser);
            intent.putExtra(Chat.SERIALIZABLE_OTHER_USER, enlargedUserDTO);
            startActivity(intent);
        });

        chatsList.addView(chatEntry);
    }

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