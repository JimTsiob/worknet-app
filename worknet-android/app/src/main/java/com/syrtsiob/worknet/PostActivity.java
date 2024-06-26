package com.syrtsiob.worknet;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syrtsiob.worknet.model.CommentDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.PostDTO;

import java.io.Serializable;

public class PostActivity extends AppCompatActivity {

    static final String POST_DTO = "POST_DTO";

    LinearLayout postContent, postComments;
    TextView postTitle, postText, likesCounter;
    EditText leaveCommentText;
    Button likeButton;
    ImageButton backButton, leaveCommentButton;

    MediaPlayer mediaPlayer;

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

        PostDTO postDTO = getIntent().getSerializableExtra(POST_DTO, PostDTO.class);
        if (postDTO != null)
            InitializePost(postDTO);
        else {
            Toast.makeText(this, "Error when loading post...", Toast.LENGTH_LONG).show();
            finish();
        }

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

        // TODO implement comment leaving to database

        // AddComment(yourNewCommentDTO);
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
            if (customFileDTO.getFileName().contains(IMAGE)) {
                ImageView imageView = new ImageView(this);
                // TODO set image URI/bitmap
                // imageView.setImageURI(); / imageView.setImageBitmap();
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                postContent.addView(imageView);
            }
            else if (customFileDTO.getFileName().contains(VIDEO)) {
                VideoView videoView = new VideoView(this);
                // TODO set video URI/path
                // videoView.setVideoURI(); videoView.setVideoPath();
                videoView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                postContent.addView(videoView);
            }
            else if (customFileDTO.getFileName().contains(AUDIO)) {
                Button newSound = new Button(this);
                newSound.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                // TODO set audio URI
                Uri soundURI = null;
                newSound.setOnClickListener(l-> {
                    mediaPlayer = MediaPlayer.create(this, soundURI);
                    mediaPlayer.start();
                });
                newSound.setText(R.string.play_sound_file);
                postContent.addView(newSound);
            }
            else {
                Toast.makeText(this, "No files were posted!", Toast.LENGTH_LONG).show();
            }
        }

        for (CommentDTO commentDTO : postDTO.getComments()) {
            AddComment(commentDTO);
        }
    }

    private void AddComment(CommentDTO commentDTO) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View comment = inflater.inflate(R.layout.post_comment_template, postComments, false);

        ImageView commenterPicture = comment.findViewById(R.id.commenter_picture);
        TextView commenterName = comment.findViewById(R.id.commenter_name);
        TextView commentText = comment.findViewById(R.id.comment_text);

        // TODO set image URI/bitmap
        // commenterPicture.setImageURI(); / commenterPicture.setImageBitmap();

        commenterName.setText(String.format("%s %s", commentDTO.getUser().getFirstName(),
                commentDTO.getUser().getLastName()));
        commentText.setText(commentDTO.getText());

        postComments.addView(comment);
    }
}