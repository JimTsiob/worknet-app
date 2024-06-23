package com.syrtsiob.worknet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syrtsiob.worknet.model.CommentDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.PostDTO;

public class PostActivity extends AppCompatActivity {

    LinearLayout postContent, postComments;
    TextView postTitle, postText, likesCounter;
    EditText leaveCommentText;
    Button likeButton;
    ImageButton backButton, leaveCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postContent = findViewById(R.id.postContent);
        postComments = findViewById(R.id.postComments);


        postTitle = findViewById(R.id.postTitle);
        postText = findViewById(R.id.postText);
        likesCounter = findViewById(R.id.likesCounter);

        leaveCommentText = findViewById(R.id.leaveCommentText);

        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(listener -> {
            // TODO database call
            int likes = Integer.parseInt(likesCounter.getText().toString());
            likesCounter.setText(String.format("%s", likes + 1));
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(listener -> finish());

        leaveCommentButton = findViewById(R.id.leaveCommentButton);
        leaveCommentButton.setOnClickListener(listener -> LeaveComment());

        OnBackPressedCallback finishWhenBackPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, finishWhenBackPressed);
    }

    private void LeaveComment() {
        String commentText = leaveCommentText.getText().toString();
        leaveCommentText.clearFocus();
        leaveCommentText.setText("");

        // TODO implement comment UI
    }


    private static final String titleSuffix = "'s Post";
    private static final String AUDIO = "audio";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private void InitializePost(PostDTO postDTO) {
        postTitle.setText(String.format("%s %s%s", postDTO.getUser().getFirstName(),
                postDTO.getUser().getLastName(), titleSuffix));
        postText.setText(postDTO.getDescription());
        likesCounter.setText(String.format("%s", postDTO.getLikes().size()));

        for (CustomFileDTO customFileDTO : postDTO.getCustomFiles()) {
            // TODO add customFile to postContent
            if (customFileDTO.getFileName().contains(IMAGE)) {
                
            }
            else if (customFileDTO.getFileName().contains(VIDEO)) {

            }
            else if (customFileDTO.getFileName().contains(AUDIO)) {

            }
            else {
                // TODO throw error
            }
        }

        /* Need to implement getComments in the backend
        for (CommentDTO commentDTO : postDTO.getComments()) {
            // TODO add to comments
        }
        */
    }
}