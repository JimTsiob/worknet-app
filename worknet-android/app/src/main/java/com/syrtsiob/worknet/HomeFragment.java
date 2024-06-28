package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.PostDTO;
import com.syrtsiob.worknet.model.SmallCustomFileDTO;
import com.syrtsiob.worknet.model.SmallPostDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.CustomFileService;
import com.syrtsiob.worknet.services.PostService;
import com.syrtsiob.worknet.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int timesServiceCalled = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout postsContainer;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            PostService postService = retrofit.create(PostService.class);
            UserService userService = retrofit.create(UserService.class);

            if (timesServiceCalled == 1){
                return;
            }

            timesServiceCalled += 1; // prevents duplicates

            userService.getUserById(userDTO.getId()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        postService.getFrontPosts(userDTO.getId()).enqueue(new Callback<List<SmallPostDTO>>() {
                            @Override
                            public void onResponse(Call<List<SmallPostDTO>> call, Response<List<SmallPostDTO>> response) {
                                if (response.isSuccessful()){

                                    if (response.body().isEmpty()){
                                        showEmptyPosts();
                                    }

                                    for (SmallPostDTO post: response.body()){
                                        addPost(post, userDTO.getId());
                                    }
                                }else{
                                    Toast.makeText(getActivity(), "Post fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<SmallPostDTO>> call, Throwable t) {
                                Log.d("post fetch fail: ", t.getLocalizedMessage());
                                Toast.makeText(getActivity(), "Post fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "User fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("user fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "User fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postsContainer = getView().findViewById(R.id.postsContainer);


        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            PostService postService = retrofit.create(PostService.class);
            UserService userService = retrofit.create(UserService.class);

            if (timesServiceCalled == 1){
                return;
            }

            timesServiceCalled += 1; // prevents duplicates

            userService.getUserById(userDTO.getId()).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()){

                        postService.getFrontPosts(userDTO.getId()).enqueue(new Callback<List<SmallPostDTO>>() {
                            @Override
                            public void onResponse(Call<List<SmallPostDTO>> call, Response<List<SmallPostDTO>> response) {
                                if (response.isSuccessful()){

                                    if (response.body().isEmpty()){
                                        showEmptyPosts();
                                    }

                                    for (SmallPostDTO post: response.body()){
                                        addPost(post, userDTO.getId());
                                    }
                                }else{
                                    Toast.makeText(getActivity(), "Post fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<SmallPostDTO>> call, Throwable t) {
                                Log.d("post fetch fail: ", t.getLocalizedMessage());
                                Toast.makeText(getActivity(), "Post fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "User fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.d("user fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "User fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void addPost(SmallPostDTO postDTO, Long userId) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View postView = inflater.inflate(R.layout.post_template, postsContainer, false);

        ImageView posterPicture = postView.findViewById(R.id.poster_picture);
        TextView posterName = postView.findViewById(R.id.poster_name);
        TextView postText = postView.findViewById(R.id.post_text);
        Button seeMoreButton = postView.findViewById(R.id.postSeeMore);

        seeMoreButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), PostActivity.class);
            intent.putExtra(PostActivity.POST_DTO_ID, postDTO.getId());
            intent.putExtra(PostActivity.USER_ID, userId);
            startActivity(intent);
        });

        String profilePicName = postDTO.getUser().getProfilePicture();
        List<SmallCustomFileDTO> files = postDTO.getUser().getFiles();
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
                        Bitmap bitmap = loadImageFromFile(response.body());
                        posterPicture.setImageBitmap(bitmap);
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

        posterName.setText(String.format("%s %s", postDTO.getUser().getFirstName(),
                postDTO.getUser().getLastName()));
        postText.setText(postDTO.getDescription());

        postsContainer.addView(postView);
    }

    private void showEmptyPosts(){
        TextView noSkillsTextView = new TextView(getActivity());
        noSkillsTextView.setText("No posts on the main page yet. \n");
        noSkillsTextView.setTextSize(20); // Set desired text size
        noSkillsTextView.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(250, 300, 16, 16);
        noSkillsTextView.setLayoutParams(params);

        postsContainer.addView(noSkillsTextView);
    }

    private Bitmap loadImageFromFile(CustomFileDTO file) {
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