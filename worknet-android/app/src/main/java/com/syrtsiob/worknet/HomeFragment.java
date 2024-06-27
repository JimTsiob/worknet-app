package com.syrtsiob.worknet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.syrtsiob.worknet.retrofit.RetrofitService;
import com.syrtsiob.worknet.services.PostService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postsContainer = getView().findViewById(R.id.postsContainer);


        UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            PostService postService = retrofit.create(PostService.class);

            postService.getFrontPosts(userDTO.getId()).enqueue(new Callback<List<PostDTO>>() {
                @Override
                public void onResponse(Call<List<PostDTO>> call, Response<List<PostDTO>> response) {
                    if (response.isSuccessful()){
                        for (PostDTO post: response.body()){
                            addPost(post);
                        }
                    }else{
                        Toast.makeText(getActivity(), "Post fetch failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<PostDTO>> call, Throwable t) {
                    Log.d("post fetch fail: ", t.getLocalizedMessage());
                    Toast.makeText(getActivity(), "Post fetch failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void addPost(PostDTO postDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View postView = inflater.inflate(R.layout.post_template, postsContainer, false);

        ImageView posterPicture = postView.findViewById(R.id.poster_picture);
        TextView posterName = postView.findViewById(R.id.poster_name);
        TextView postText = postView.findViewById(R.id.post_text);
        Button seeMoreButton = postView.findViewById(R.id.postSeeMore);

        seeMoreButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), PostActivity.class);
            intent.putExtra(PostActivity.POST_DTO_ID, postDTO.getId());
            startActivity(intent);
        });

        String profilePicName = postDTO.getUser().getProfilePicture();
        List<CustomFileDTO> files = postDTO.getUser().getFiles();
        Optional<CustomFileDTO> profilePicture = files.stream()
                .filter(file -> file.getFileName().equals(profilePicName))
                .findFirst();

        if (profilePicture.isPresent()){
            Bitmap bitmap = loadImageFromFile(profilePicture.get());
            posterPicture.setImageBitmap(bitmap);
        }

        // TODO set image
        // posterPicture.setImageURI(); / posterPicture.setImageBitmap();
        posterName.setText(String.format("%s %s", postDTO.getUser().getFirstName(),
                postDTO.getUser().getLastName()));
        postText.setText(postDTO.getDescription());

        postsContainer.addView(postView);
    }

    private Bitmap loadImageFromFile(CustomFileDTO file) {
        InputStream inputStream = decodeBase64ToInputStream(file.getFileContent());
        return BitmapFactory.decodeStream(inputStream);
    }

    // used to decode the image's base64 string from the db.
    private InputStream decodeBase64ToInputStream(String base64Data) {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        return new ByteArrayInputStream(bytes);
    }

}