package com.syrtsiob.worknet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.enums.NotificationType;
import com.syrtsiob.worknet.model.CommentDTO;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.LikeDTO;
import com.syrtsiob.worknet.model.NotificationDTO;
import com.syrtsiob.worknet.model.PostDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.SmallPostDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.UserLikeDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CommentService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.LikeService;
import com.syrtsiob.worknet.services.NotificationService;
import com.syrtsiob.worknet.services.PostService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostActivity extends AppCompatActivity {

    static final String POST_DTO_ID = "POST_DTO_ID";

    static final String USER_ID = "USER_ID";

    private ProgressBar progressBar;
    private View progressOverlay;

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

        progressBar = findViewById(R.id.progress_bar);
        progressOverlay = findViewById(R.id.progress_overlay);



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
        showProgress(true);

        postService.getPostById(postId).enqueue(new Callback<PostDTO>() {

            @Override
            public void onResponse(Call<PostDTO> call, Response<PostDTO> response) {
                showProgress(false);
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
                showProgress(false);
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

        if (commentText.isEmpty()){
            Toast.makeText(this, "Comment text cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        leaveCommentText.clearFocus();
        leaveCommentText.setText("");

        Long userId = getIntent().getLongExtra(USER_ID, 0L);
        Long postId = getIntent().getLongExtra(POST_DTO_ID, 0L);

        UserDtoResultLiveData.getInstance().observe(PostActivity.this, userDTO -> {
            EnlargedUserDTO enlargedUserDTO = new EnlargedUserDTO();
            enlargedUserDTO.setId(userId);
            enlargedUserDTO.setProfilePicture(userDTO.getProfilePicture());
            enlargedUserDTO.setLastName(userDTO.getLastName());
            enlargedUserDTO.setFirstName(userDTO.getFirstName());
            enlargedUserDTO.setFiles(userDTO.getFiles());

            SmallPostDTO smallPostDTO = new SmallPostDTO();
            smallPostDTO.setUser(postUser);
            smallPostDTO.setPostCreationDate(postCreationDate);
            smallPostDTO.setId(postId);
            smallPostDTO.setDescription(postDescription);

            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setPost(smallPostDTO);
            commentDTO.setText(commentText);
            commentDTO.setUser(enlargedUserDTO);

            Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
            CommentService commentService = retrofit.create(CommentService.class);
            NotificationService notificationService = retrofit.create(NotificationService.class);


            SmallUserDTO postUserSmall = new SmallUserDTO();
            postUserSmall.setFirstName(postUser.getFirstName());
            postUserSmall.setId(postUser.getId());
            postUserSmall.setLastName(postUser.getLastName());



            commentService.addComment(commentDTO).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(PostActivity.this, "Comment added successfully.", Toast.LENGTH_LONG).show();
                        AddComment(commentDTO);

                        // if user comments on their own post do not send notification
                        if (Objects.equals(postUserSmall.getId(), enlargedUserDTO.getId())){
                            return;
                        }

                        NotificationDTO notificationDTO = new NotificationDTO();
                        NotificationType notificationType = NotificationType.valueOf("COMMENT");
                        notificationDTO.setNotificationType(notificationType);
                        notificationDTO.setPost(smallPostDTO);
                        notificationDTO.setReceiver(postUserSmall);

                        notificationDTO.setSender(enlargedUserDTO);

                        notificationDTO.setText(enlargedUserDTO.getFirstName() + " " +
                                enlargedUserDTO.getLastName() + " has commented on your post.");

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


                    }else{
                        Toast.makeText(PostActivity.this, "Comment addition failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("comment fail: ", t.getLocalizedMessage());
                    Toast.makeText(PostActivity.this, "Comment addition failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        });
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
                Bitmap bitmap = loadImageFromFile(customFileDTO);
                imageView.setImageBitmap(bitmap);


                // Set specific width and height in pixels
                int width = 400;
                int height = 400;
                imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));

                postContent.addView(imageView);
            }
            else if (customFileDTO.getFileName().contains(VIDEO)) {
                VideoView videoView = new VideoView(this);
                int width = 900;
                int height = 900;

//                videoView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        width,
                        height
                );

                layoutParams.setMargins(0, 20, 0, 0);
                videoView.setLayoutParams(layoutParams);

                // get URI from video file and use it.
                try {
                    InputStream inputStream = decodeBase64ToInputStream(customFileDTO.getFileContent());

                    File file = saveInputStreamAsFile(this, inputStream, customFileDTO.getFileName());
                    Uri videoUri = Uri.fromFile(file);
                    videoView.setVideoURI(videoUri);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    postContent.addView(videoView);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (customFileDTO.getFileName().contains(AUDIO)) {
                Button newSound = new Button(this);
                newSound.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                //  get Uri from audio file and use the play sound file buttons to play them.
                try {
                    InputStream inputStream = decodeBase64ToInputStream(customFileDTO.getFileContent());

                    File file = saveInputStreamAsFile(this, inputStream, customFileDTO.getFileName());
                    Uri soundURI = Uri.fromFile(file);
                    newSound.setOnClickListener(l-> {
                        mediaPlayer = MediaPlayer.create(this, soundURI);
                        mediaPlayer.start();
                    });
                    newSound.setText(R.string.play_sound_file);
                    postContent.addView(newSound);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

        String profilePicName = commentDTO.getUser().getProfilePicture();

        List<SmallCustomFileDTO> files = commentDTO.getUser().getFiles();
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
                        Bitmap bitmap = loadImageFromFile(response.body());
                        commenterPicture.setImageBitmap(bitmap);
                    }else{
                        Toast.makeText(PostActivity.this, "File fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CustomFileDTO> call, Throwable t) {
                    Log.d("file fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(PostActivity.this, "File fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        }

        commenterName.setText(String.format("%s %s", commentDTO.getUser().getFirstName(),
                commentDTO.getUser().getLastName()));
        commentText.setText(commentDTO.getText());

        postComments.addView(comment);
    }

    // hide post content and show progress spinner.
    private void showProgress(boolean show) {
        progressOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        postContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // method that returns images from the db.
    private Bitmap loadImageFromFile(CustomFileDTO file) {
        InputStream inputStream = decodeBase64ToInputStream(file.getFileContent());
        return BitmapFactory.decodeStream(inputStream);
    }

    // used for getting the URI out of files
    public File saveInputStreamAsFile(Context context, InputStream inputStream, String fileName) throws IOException {
        File file = new File(context.getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
        } finally {
            inputStream.close();
        }
        return file;
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