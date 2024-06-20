package com.syrtsiob.worknet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.model.MessageDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    LinearLayout messages;
    ScrollView scrollView;

    ImageButton buttonSend;
    ImageButton buttonBack;
    EditText editTextMessage;

    ImageView chatImage;
    TextView chatUserName;

    SmallUserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = findViewById(R.id.messages);
        scrollView = findViewById(R.id.scrollView);

        chatImage = findViewById(R.id.chatImage);
        chatUserName = findViewById(R.id.chatUserName);

        editTextMessage = findViewById(R.id.editTextMessage);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(l -> { finish(); });

        buttonSend = findViewById(R.id.buttonSend);

        // TODO set current user - SmallUserDTO

        // TODO also set profile pic and name of other user

        // TODO call populate chat with messages' arraylist
        TestPopulateChat();

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

    // Should always be called after chat interaction
    private void ResetScrollView() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void PopulateChat(ArrayList<MessageDTO> messageList) {
        for (MessageDTO message : messageList) {
            AddNewMessage(message);
        }
    }

    private void AddNewMessage(MessageDTO message) {
        TextView newMessage = new TextView(this);

        newMessage.setText(message.getText());
        newMessage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if(currentUser != message.getUsers().get(0))
            newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        else
            newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        messages.addView(newMessage);
        ResetScrollView();
    }

    private void TestPopulateChat() {
        for (int i = 0; i <50; i++) {
            TextView newMessage = new TextView(this);

            newMessage.setText("Message " + i);

            if(i%2==0)
                newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            else
                newMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            messages.addView(newMessage);
            ResetScrollView();
        }
    }
}