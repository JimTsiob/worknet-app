package com.syrtsiob.worknet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostFragment newInstance() {
        return new PostFragment();
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
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    LinearLayout addPostView;
    EditText postText;
    Button addImage, addVideo, addSound, addPost;

    ArrayList<Uri> imagesToPost, videosToPost, soundFilesToPost;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagesToPost = new ArrayList<>();
        videosToPost = new ArrayList<>();
        soundFilesToPost = new ArrayList<>();

        addPostView = requireView().findViewById(R.id.addPostView);

        postText = requireView().findViewById(R.id.postText);

        addImage = requireView().findViewById(R.id.postImage);
        addVideo = requireView().findViewById(R.id.postVideo);
        addSound = requireView().findViewById(R.id.postSound);
        addPost = requireView().findViewById(R.id.addPost);

        addPost.setOnClickListener(listener -> {
            AttemptPost();
        });

        // =========================================================================================
        // Image picker
        ActivityResultLauncher<Intent> pickImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImageURI = Objects.requireNonNull(data).getData();
                        imagesToPost.add(selectedImageURI);

                        TextView newImageUri = new TextView(getContext());
                        if (selectedImageURI != null) {
                            newImageUri.setText(selectedImageURI.toString());
                            addPostView.addView(newImageUri);
                        }
                    }
                });
        addImage.setOnClickListener(listener ->{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            pickImageActivity.launch(photoPickerIntent);
        });

        // =========================================================================================
        // Video picker
        ActivityResultLauncher<Intent> pickVideoActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedVideoURI = Objects.requireNonNull(data).getData();
                        videosToPost.add(selectedVideoURI);

                        TextView newVideoURI = new TextView(getContext());
                        if (selectedVideoURI != null) {
                            newVideoURI.setText(selectedVideoURI.toString());
                            addPostView.addView(newVideoURI);
                        }
                    }
                });
        addVideo.setOnClickListener(listener ->{
            Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
            videoPickerIntent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"video/*");
            pickVideoActivity.launch(videoPickerIntent);
        });

        // =========================================================================================
        // Sound picker
        ActivityResultLauncher<Intent> pickSoundActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedSoundURI = Objects.requireNonNull(data).getData();
                        soundFilesToPost.add(selectedSoundURI);

                        TextView newSoundURI = new TextView(getContext());
                        if (selectedSoundURI != null) {
                            newSoundURI.setText(selectedSoundURI.toString());
                            addPostView.addView(newSoundURI);
                        }
                    }
                });
        addSound.setOnClickListener(listener ->{
            Intent soundPickerIntent = new Intent(Intent.ACTION_PICK);
            soundPickerIntent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            pickSoundActivity.launch(soundPickerIntent);
        });
    }

    private void AttemptPost() {
        // TODO implement using arraylists of URIs and the editText
    }
}