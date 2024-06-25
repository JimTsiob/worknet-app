package com.syrtsiob.worknet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
        addPost();
    }

    // TODO Add post content and functionality
    private void addPost() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View postView = inflater.inflate(R.layout.post_template, postsContainer, false);

        // TODO add images

        TextView postText = postView.findViewById(R.id.post_text);
        Button likeButton = postView.findViewById(R.id.post_like_button);
        Button commentsButton = postView.findViewById(R.id.post_comments_button);

        commentsButton.setOnClickListener(listener -> {
            postPopup(postsContainer);
        });

        postsContainer.addView(postView);
    }

    private void postPopup(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View popupView = inflater.inflate(R.layout.post_popup_template, postsContainer, false);

        LinearLayout postPopupCommentContainer = popupView.findViewById(R.id.postPopupCommentContainer);
        for (int i = 0; i < 15; i++) {
            View comment = inflater.inflate(R.layout.post_comment_template, postPopupCommentContainer, false);
            TextView commentText = comment.findViewById(R.id.comment_text);
            commentText.setText("Comment " + i);
            postPopupCommentContainer.addView(comment);
        }

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}