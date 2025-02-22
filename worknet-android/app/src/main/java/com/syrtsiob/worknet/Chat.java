package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.MessageDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.MessageService;
import com.syrtsiob.worknet.services.NotificationService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Chat extends AppCompatActivity {

    LinearLayout messages;

    LinearLayout purpleBox;

    LinearLayout silverBox;
    ScrollView scrollView;

    ImageButton buttonSend;
    ImageButton buttonBack;
    EditText editTextMessage;

    ImageView chatImage;
    TextView chatUserName;

    EnlargedUserDTO loggedInUser;

    EnlargedUserDTO otherUser;

    static final String SERIALIZABLE_LOGGED_IN_USER = "serializable_logged_in_user";

    static final String SERIALIZABLE_OTHER_USER = "serializable_other_user";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = findViewById(R.id.messages);

        purpleBox = findViewById(R.id.purpleBox);

        silverBox = findViewById(R.id.silverBox);

        purpleBox.setVisibility(View.GONE);
        silverBox.setVisibility(View.GONE);

        scrollView = findViewById(R.id.scrollView);

        chatImage = findViewById(R.id.chatImage);
        chatUserName = findViewById(R.id.chatUserName);

        editTextMessage = findViewById(R.id.editTextMessage);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(l -> {
            // this is done so we can get same result after using the send message button in profiles.
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(getString(R.string.e_mail), loggedInUser.getEmail());
            intent.putExtra("fragment_to_replace", "message_fragment");
            startActivity(intent);
//            finish();
        });

        buttonSend = findViewById(R.id.buttonSend);

        loggedInUser = getIntent().getSerializableExtra(SERIALIZABLE_LOGGED_IN_USER, EnlargedUserDTO.class);

        otherUser = getIntent().getSerializableExtra(SERIALIZABLE_OTHER_USER, EnlargedUserDTO.class);

        String profilePicName = otherUser.getProfilePicture();
        List<SmallCustomFileDTO> files = otherUser.getFiles();
        Optional<SmallCustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        CustomFileService customFileService = retrofit.create(CustomFileService.class);

        if (profilePicture.isPresent()){
            customFileService.getCustomFileById(profilePicture.get().getId()).enqueue(new Callback<CustomFileDTO>() {
                @Override
                public void onResponse(Call<CustomFileDTO> call, Response<CustomFileDTO> response) {
                    if (response.isSuccessful()){
                        Bitmap bitmap = loadImageFromConnectionFile(response.body());
                        chatImage.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(Chat.this, "File fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CustomFileDTO> call, Throwable t) {
                    Log.d("file fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(Chat.this, "File fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        }



        chatUserName.setText("Chat with " + otherUser.getFirstName() + " " + otherUser.getLastName());

        MessageService messageService = retrofit.create(MessageService.class);

        messageService.getAllMessages().enqueue(new Callback<List<MessageDTO>>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()){
                    // get only messages with our two users present
                    List<MessageDTO> filteredMessages = new ArrayList<>();

                    for (MessageDTO messageDTO: response.body()){
                        if (filterMessage(messageDTO)) {
                            filteredMessages.add(messageDTO);
                        }
                    }

                    // sort by ID ascendingly to show the messages in order.
                    filteredMessages = filteredMessages.stream()
                            .sorted(Comparator.comparing(MessageDTO::getId))
                            .collect(Collectors.toList());

                    PopulateChat(filteredMessages);

                    buttonSend.setOnClickListener(listener -> {

                        if (editTextMessage.getText().toString().isEmpty()){
                            Toast.makeText(Chat.this, "message text cannot be empty.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setText(editTextMessage.getText().toString());
                        messageDTO.setReceiver(otherUser);
                        messageDTO.setSender(loggedInUser);

                        NotificationService notificationService = retrofit.create(NotificationService.class);

                        messageService.addMessage(messageDTO).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()){
                                    Toast.makeText(Chat.this, "message sent successfully.", Toast.LENGTH_LONG).show();

                                    NotificationDTO notificationDTO = new NotificationDTO();
                                    NotificationType notificationType = NotificationType.valueOf("MESSAGE");
                                    notificationDTO.setNotificationType(notificationType);

                                    SmallUserDTO smallOtherUserDTO = new SmallUserDTO();
                                    smallOtherUserDTO.setId(otherUser.getId());
                                    smallOtherUserDTO.setFirstName(otherUser.getFirstName());
                                    smallOtherUserDTO.setLastName(otherUser.getLastName());

                                    notificationDTO.setReceiver(smallOtherUserDTO);
                                    notificationDTO.setSender(loggedInUser);

                                    String notificationText = loggedInUser.getFirstName() + " " + loggedInUser.getLastName() + " has sent you a message.";
                                    notificationDTO.setText(notificationText);


                                    notificationService.addNotification(notificationDTO).enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.isSuccessful()){
                                                // print nothing
//                                    replaceFragment(MessagesFragment.newInstance());
                                                Intent intent = new Intent(Chat.this, Chat.class);
                                                intent.putExtra(Chat.SERIALIZABLE_LOGGED_IN_USER, loggedInUser);
                                                intent.putExtra(Chat.SERIALIZABLE_OTHER_USER, otherUser);
                                                startActivity(intent);

                                            }else{
                                                Toast.makeText(Chat.this, "Notification addition failed! Check the format.", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.d("message notification failure: ", t.getLocalizedMessage());
                                            Toast.makeText(Chat.this, "Notification addition failed! Server failure.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(Chat.this, "message sending failed. Check the format.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("message failure: ", t.getLocalizedMessage());
                                Toast.makeText(Chat.this, "message sending failed. Server failure.", Toast.LENGTH_LONG).show();
                            }
                        });
                    });

                }else{
                    Toast.makeText(Chat.this, "Failed message server fetch. Check the format.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable t) {
                Log.d("message failure: ", t.getLocalizedMessage());
                Toast.makeText(Chat.this, "Failed message server fetch. Server failure.", Toast.LENGTH_LONG).show();
            }
        });

        OnBackPressedCallback finishWhenBackPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, finishWhenBackPressed);

        ResetScrollView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ResetScrollView();
    }


    /**
     * Finds all messages with our loggedin User and the other chat user.
     * */
    private boolean filterMessage(MessageDTO message){
        return (Objects.equals(message.getSender().getId(), loggedInUser.getId()) && Objects.equals(message.getReceiver().getId(), otherUser.getId())
        || (Objects.equals(message.getSender().getId(), otherUser.getId()) && Objects.equals(message.getReceiver().getId(), loggedInUser.getId())));
    }

    // Should always be called after chat interaction
    private void ResetScrollView() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void PopulateChat(List<MessageDTO> messageList) {
        for (MessageDTO message : messageList) {
            AddNewMessage(message);
        }
    }

    private void AddNewMessage(MessageDTO message) {
        TextView newMessage = new TextView(this);

        if ((Objects.equals(message.getSender().getId(), loggedInUser.getId()))){
            newMessage.setText("You: " + message.getText());
        }else{
            newMessage.setText(message.getSender().getFirstName() + " " + message.getSender().getLastName() + ": " + message.getText());
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 50, 0, 0);

        if(!(Objects.equals(message.getSender().getId(), loggedInUser.getId())))
            newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        else
            newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        LinearLayout purpleSquare = new LinearLayout(getApplicationContext());
        purpleSquare.setLayoutParams(purpleBox.getLayoutParams()); // Copy layout params
        purpleSquare.setOrientation(purpleBox.getOrientation()); // Copy orientation (optional)

        int yellowColor = getApplicationContext().getResources().getColor(R.color.purple); // Get color from resources
        purpleSquare.setBackgroundColor(yellowColor);

        purpleSquare.setPadding(purpleBox.getPaddingLeft(), purpleBox.getPaddingTop(), purpleBox.getPaddingRight(), purpleBox.getPaddingBottom());

        LinearLayout silverSquare = new LinearLayout(getApplicationContext());
        silverSquare.setLayoutParams(silverBox.getLayoutParams());
        silverSquare.setOrientation(silverBox.getOrientation());

        int silverColor = getApplicationContext().getResources().getColor(R.color.silver);
        silverSquare.setBackgroundColor(silverColor);

        silverSquare.setPadding(silverBox.getPaddingLeft(), silverBox.getPaddingTop(), silverBox.getPaddingRight(), silverBox.getPaddingBottom());


        newMessage.setTextSize(20);
        newMessage.setLayoutParams(params);

        // add other user's messages in the yellow box, our logged in user in the silver box.
        if(!(Objects.equals(message.getSender().getId(), loggedInUser.getId()))){
            purpleSquare.addView(newMessage);
            newMessage.setTextColor(getResources().getColor(R.color.white));
            messages.addView(purpleSquare);
        }
        else{
            silverSquare.addView(newMessage);
            messages.addView(silverSquare);
        }

        ResetScrollView();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
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