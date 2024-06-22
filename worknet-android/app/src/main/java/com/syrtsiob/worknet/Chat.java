package com.syrtsiob.worknet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.JobDTO;
import com.syrtsiob.worknet.model.MessageDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Chat extends AppCompatActivity {

    LinearLayout messages;

    LinearLayout yellowBox;

    LinearLayout silverBox;
    ScrollView scrollView;

    ImageButton buttonSend;
    ImageButton buttonBack;
    EditText editTextMessage;

    ImageView chatImage;
    TextView chatUserName;

    EnlargedUserDTO loggedInUser;

    EnlargedUserDTO otherUser;

    UserDTO loggedInUserDto;

    static final String SERIALIZABLE_LOGGED_IN_USER = "serializable_logged_in_user";

    static final String SERIALIZABLE_OTHER_USER = "serializable_other_user";

    static final String SERIALIZABLE_LOGGED_IN_USER_DTO = "serializable_logged_in_user_dto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = findViewById(R.id.messages);

        yellowBox = findViewById(R.id.yellowBox);

        silverBox = findViewById(R.id.silverBox);

        yellowBox.setVisibility(View.GONE);
        silverBox.setVisibility(View.GONE);

        scrollView = findViewById(R.id.scrollView);

        chatImage = findViewById(R.id.chatImage);
        chatUserName = findViewById(R.id.chatUserName);

        editTextMessage = findViewById(R.id.editTextMessage);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(l -> { finish(); });

        buttonSend = findViewById(R.id.buttonSend);

        loggedInUser = getIntent().getSerializableExtra(SERIALIZABLE_LOGGED_IN_USER, EnlargedUserDTO.class);

        otherUser = getIntent().getSerializableExtra(SERIALIZABLE_OTHER_USER, EnlargedUserDTO.class);

        loggedInUserDto = getIntent().getSerializableExtra(SERIALIZABLE_LOGGED_IN_USER_DTO, UserDTO.class);

        String profilePicName = otherUser.getProfilePicture();
        List<CustomFileDTO> files = otherUser.getFiles();
        Optional<CustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        if (profilePicture.isPresent()){
            Bitmap bitmap = loadImageFromConnectionFile(profilePicture.get());
            chatImage.setImageBitmap(bitmap);
        }

        chatUserName.setText("Chat with " + otherUser.getFirstName() + " " + otherUser.getLastName());

        List<MessageDTO> receivedMessages = loggedInUserDto.getReceivedMessages();

        List<MessageDTO> sentMessages = loggedInUserDto.getSentMessages();

        List<MessageDTO> allMessages = new ArrayList<>();

        allMessages.addAll(receivedMessages);
        allMessages.addAll(sentMessages);

        // get only messages with our two users present
        List<MessageDTO> filteredMessages = new ArrayList<>();

        for (MessageDTO messageDTO: allMessages){
            if (filterMessage(messageDTO)) {
                filteredMessages.add(messageDTO);
            }
        }

        // sort by ID ascendingly to show the messages in order.
        filteredMessages = filteredMessages.stream()
                .sorted(Comparator.comparing(MessageDTO::getId))
                .collect(Collectors.toList());

        PopulateChat(filteredMessages);

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

        LinearLayout yellowSquare = new LinearLayout(getApplicationContext());
        yellowSquare.setLayoutParams(yellowBox.getLayoutParams()); // Copy layout params
        yellowSquare.setOrientation(yellowBox.getOrientation()); // Copy orientation (optional)

        int yellowColor = getApplicationContext().getResources().getColor(R.color.yellow); // Get color from resources
        yellowSquare.setBackgroundColor(yellowColor);

        yellowSquare.setPadding(yellowBox.getPaddingLeft(), yellowBox.getPaddingTop(), yellowBox.getPaddingRight(), yellowBox.getPaddingBottom());

        LinearLayout silverSquare = new LinearLayout(getApplicationContext());
        silverSquare.setLayoutParams(silverBox.getLayoutParams());
        silverSquare.setOrientation(silverBox.getOrientation());

        int silverColor = getApplicationContext().getResources().getColor(R.color.silver);
        silverSquare.setBackgroundColor(silverColor);

        silverSquare.setPadding(silverBox.getPaddingLeft(), silverBox.getPaddingTop(), silverBox.getPaddingRight(), silverBox.getPaddingBottom());


        newMessage.setTextSize(20);
        newMessage.setLayoutParams(params);

        if(!(Objects.equals(message.getSender().getId(), loggedInUser.getId()))){
            yellowSquare.addView(newMessage);
            messages.addView(yellowSquare);
        }
        else{
            silverSquare.addView(newMessage);
            messages.addView(silverSquare);
        }

        ResetScrollView();
    }

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