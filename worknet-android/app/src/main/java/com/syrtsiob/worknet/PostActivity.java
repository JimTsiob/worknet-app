package com.syrtsiob.worknet;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CommentDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.LikeDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.PostDTO;
import com.syrtsiob.worknet.model.SmallPostDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.UserLikeDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.LikeService;
import com.syrtsiob.worknet.services.NotificationService;
import com.syrtsiob.worknet.services.PostService;
import com.syrtsiob.worknet.services.UserService;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostActivity extends AppCompatActivity {

    static final String POST_DTO_ID = "POST_DTO_ID";

    static final String USER_ID = "USER_ID";

    LinearLayout postContent, postComments;
    TextView postTitle, postText, likesCounter;
    EditText leaveCommentText;
    Button likeButton;
    ImageButton backButton, leaveCommentButton;

    String postDescription, postCreationDate; // used for likes service below

    EnlargedUserDTO postUser; // used for likes service below

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postContent = findViewById(R.id.postContent);
        postComments = findViewById(R.id.postComments);

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        Long postId = getIntent().getLongExtra(POST_DTO_ID, 0L);
        Long userId = getIntent().getLongExtra(USER_ID, 0L);

        // if post is already liked, hide the button
        userService.getUserById(userId).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()){
                    List<UserLikeDTO> likes = response.body().getLikes();

                    for (UserLikeDTO like: likes){
                        if (Objects.equals(like.getPost().getId(), postId)){
                            likeButton.setEnabled(false);
                        }
                    }

                }else{
                    Toast.makeText(PostActivity.this, "User fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.d("user fetch fail: ",t.getLocalizedMessage());
                Toast.makeText(PostActivity.this, "User fetch failed. Server failure.", Toast.LENGTH_LONG).show();
            }
        });


        postTitle = findViewById(R.id.postTitle);
        postText = findViewById(R.id.postText);
        likesCounter = findViewById(R.id.likesCounter);

        leaveCommentText = findViewById(R.id.leaveCommentText);

        likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(listener -> {

            LikeService likeService = retrofit.create(LikeService.class);
            NotificationService notificationService = retrofit.create(NotificationService.class);

            LikeDTO likeDTO = new LikeDTO();
            SmallPostDTO smallPostDTO = new SmallPostDTO();
            smallPostDTO.setUser(postUser);
            smallPostDTO.setPostCreationDate(postCreationDate);
            smallPostDTO.setId(postId);
            smallPostDTO.setDescription(postDescription);

            SmallUserDTO smallUserDTO = new SmallUserDTO();
            smallUserDTO.setFirstName(postUser.getFirstName());
            smallUserDTO.setId(postUser.getId());
            smallUserDTO.setLastName(postUser.getLastName());

            likeDTO.setPost(smallPostDTO);
            likeDTO.setUser(smallUserDTO);

            likeService.addLike(likeDTO).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(PostActivity.this, "Post liked.", Toast.LENGTH_LONG).show();
                        likeButton.setEnabled(false);

                        NotificationDTO notificationDTO = new NotificationDTO();
                        NotificationType notificationType = NotificationType.valueOf("LIKE_POST");
                        notificationDTO.setNotificationType(notificationType);
                        notificationDTO.setPost(smallPostDTO);
                        notificationDTO.setReceiver(smallUserDTO);

                        UserDtoResultLiveData.getInstance().observe(PostActivity.this, userDTO -> {

                            // if logged in user liked their own post, return.
                            if (Objects.equals(userDTO.getId(), postUser.getId())){
                                return;
                            }

                            EnlargedUserDTO enlargedUserDTO = new EnlargedUserDTO();
                            enlargedUserDTO.setId(userDTO.getId());
                            enlargedUserDTO.setEducations(userDTO.getEducations());
                            enlargedUserDTO.setEmail(userDTO.getEmail());
                            enlargedUserDTO.setSkills(userDTO.getSkills());
                            enlargedUserDTO.setWorkExperiences(userDTO.getWorkExperiences());
                            enlargedUserDTO.setProfilePicture(userDTO.getProfilePicture());
                            enlargedUserDTO.setFirstName(userDTO.getFirstName());
                            enlargedUserDTO.setLastName(userDTO.getLastName());
                            enlargedUserDTO.setFiles(userDTO.getFiles());

                            notificationDTO.setSender(enlargedUserDTO);

                            notificationDTO.setText(enlargedUserDTO.getFirstName() + " " +
                                    enlargedUserDTO.getLastName() + " has liked your post.");

                            notificationService.addNotification(notificationDTO).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()){
                                        // do nothing
                                    }else{
                                        Toast.makeText(PostActivity.this, "Notification failed. Check the format.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("notification fail:", t.getLocalizedMessage());
                                    Toast.makeText(PostActivity.this, "Notification failed. Server failure.", Toast.LENGTH_LONG).show();
                                }
                            });

                        });

                    }else{
                        Toast.makeText(PostActivity.this, "Like failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("like addition fail: ", t.getLocalizedMessage());
                    Toast.makeText(PostActivity.this, "Like failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });

            int likes = Integer.parseInt(likesCounter.getText().toString());
            likesCounter.setText(String.format("%s", likes + 1));
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(listener -> finish());

        leaveCommentButton = findViewById(R.id.leaveCommentButton);
        leaveCommentButton.setOnClickListener(listener -> LeaveComment());




        PostService postService = retrofit.create(PostService.class);

        postService.getPostById(postId).enqueue(new Callback<PostDTO>() {
            @Override
            public void onResponse(Call<PostDTO> call, Response<PostDTO> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        postUser = response.body().getUser();
                        postCreationDate = response.body().getPostCreationDate();
                        postDescription = response.body().getDescription();

                        InitializePost(response.body());
                    }
                    else {
                        Toast.makeText(PostActivity.this, "Error when loading post...", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else{
                    Toast.makeText(PostActivity.this, "Post fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostDTO> call, Throwable t) {
                Log.d("post fail: ", t.getLocalizedMessage());
                Toast.makeText(PostActivity.this, "Post fetch failed. Server failure.", Toast.LENGTH_LONG).show();
            }
        });

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