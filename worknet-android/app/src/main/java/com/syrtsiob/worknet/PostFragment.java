package com.syrtsiob.worknet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.EnlargedUserDTO;
import com.syrtsiob.worknet.model.PostDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.PostService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
            AttemptUploadFiles();
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
            Intent soundPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            soundPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
            soundPickerIntent.setType("audio/*");  // Specify the MIME type for audio files

            // Optionally, restrict to local storage if needed
            soundPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

            pickSoundActivity.launch(soundPickerIntent);
        });

    }

    private void AttemptPost() {
        // TODO implement using arraylists of URIs and the editText
        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        PostService postService = retrofit.create(PostService.class);

        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            EnlargedUserDTO enlargedUserDTO = new EnlargedUserDTO();
            enlargedUserDTO.setFiles(userDTO.getFiles());
            enlargedUserDTO.setId(userDTO.getId());
            enlargedUserDTO.setProfilePicture(userDTO.getProfilePicture());
            enlargedUserDTO.setSkills(userDTO.getSkills());
            enlargedUserDTO.setEducations(userDTO.getEducations());
            enlargedUserDTO.setEmail(userDTO.getEmail());
            enlargedUserDTO.setLastName(userDTO.getLastName());
            enlargedUserDTO.setFirstName(userDTO.getFirstName());
            enlargedUserDTO.setWorkExperiences(userDTO.getWorkExperiences());

            PostDTO postDTO = new PostDTO();
            postDTO.setDescription(postText.getText().toString());
            postDTO.setUser(enlargedUserDTO);

            // Define formatter and today's date as post creation date to db.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate currentDate = LocalDate.now();
            String formattedDate = currentDate.format(formatter);

            postDTO.setPostCreationDate(formattedDate);

            postService.addPost(postDTO).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getActivity(), "post created successfully!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "post creation failed! Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), "post creation failed! Server failure.", Toast.LENGTH_LONG).show();
                }
            });

        });

    }

    private void AttemptUploadFiles() {
        try{
            ArrayList<File> files = createTempFilesFromUris(imagesToPost, videosToPost, soundFilesToPost);

            if (files.isEmpty()){
                return;
            }

            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

                Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
                PostService postService = retrofit.create(PostService.class);

                postService.getAllPosts().enqueue(new Callback<List<PostDTO>>() {
                    @Override
                    public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                        if (response.isSuccessful()){
                            // get the id of the latest created post, which is the post we want to upload files.
                            Optional<Long> maxId = response.body().stream()
                                    .map(PostDTO::getId)
                                    .max(Long::compare);

                            if (maxId.isPresent()){
                                uploadFiles(files, userDTO.getId(), maxId.get());
                            }else{ // in case there is only one Post in the DB.
                                PostDTO postDTO = response.body().get(0);
                                uploadFiles(files, userDTO.getId(), postDTO.getId());
                            }

                            replaceFragment(HomeFragment.newInstance()); //  return to home screen

                        }else{
                            Toast.makeText(getActivity(), "Files upload failed! Check the format.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                        Toast.makeText(getActivity(), "Files upload failed! Server failure.", Toast.LENGTH_LONG).show();
                    }
                });
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Method used for creating File types from URIs to turn them to CustomFileDTOs later on for uploading.*/
    private ArrayList<File> createTempFilesFromUris(ArrayList<Uri> images, ArrayList<Uri> videos, ArrayList<Uri> audio) throws IOException {
        ArrayList<File> postFiles = new ArrayList<>();

        // add all files to the arraylist and return it for uploading.
        if (!images.isEmpty()){
            int imageCounter = 0;
            for (Uri imageUri: images){
                File file = new File(getActivity().getCacheDir(),  "image_" + imageCounter + ".jpg");

                try (InputStream inputStream =  getActivity().getContentResolver().openInputStream(imageUri);
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[4 * 1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                    }
                }
                postFiles.add(file);
                imageCounter++;
            }
        }

        if (!videos.isEmpty()){
            int videoCounter = 0; // this is just used for naming files.
            for (Uri videoUri: videos){
                File file = new File(getActivity().getCacheDir(),  "video_" + videoCounter + ".mp4");

                try (InputStream inputStream =  getActivity().getContentResolver().openInputStream(videoUri);
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[4 * 1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                    }
                }
                postFiles.add(file);
                videoCounter++;
            }
        }

        if (!audio.isEmpty()){
            int audioCounter = 0; // this is just used for naming files.
            for (Uri audioUri: audio){
                File file = new File(getActivity().getCacheDir(),  "audio_" + audioCounter + ".mp3");

                try (InputStream inputStream =  getActivity().getContentResolver().openInputStream(audioUri);
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[4 * 1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                    }
                }
                postFiles.add(file);
                audioCounter++;
            }
        }

        return postFiles;
    }

    /**
     * Method used for turning File types to CustomFileDTOs and uploading them to DB. */
    private void uploadFiles(ArrayList<File> filesForPost, Long userId, Long postId) {

        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        CustomFileService customFileService = retrofit.create(CustomFileService.class);

        List<MultipartBody.Part> files = new ArrayList<>();

        for (File file: filesForPost){
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
            files.add(body);
        }

        customFileService.uploadPostFiles(files, userId, postId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("Upload success", "Files uploaded successfully.");
                } else {
                    Toast.makeText(getActivity(), "Files upload failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(getActivity(), "Files upload failed! Server failure.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}