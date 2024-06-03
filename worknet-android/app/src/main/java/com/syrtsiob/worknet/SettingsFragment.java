package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.RegisterResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserEmailResultLiveData;
import com.syrtsiob.worknet.interfaces.UserService;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    Button cancelButton;
    Button submitButton;

    EditText emailEdit;
    EditText passwordEdit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private void AttemptDataChange(UserDTO user) {
        String newEmail = emailEdit.getText().toString();
        String newPassword = passwordEdit.getText().toString();

        if (newEmail.isEmpty()){
            user.setEmail(newPassword);

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.updateUser(user.getId(), user).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "update successful.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "update failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("fail: ", t.getLocalizedMessage());
                    // Handle the error
                    Toast.makeText(getActivity(), "update failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        // enter only email for update.
        if (newPassword.isEmpty()){
            if(!ValidateEmail(newEmail))
                return;

            user.setEmail(newEmail);

            Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
            UserService userService = retrofit.create(UserService.class);

            userService.updateUser(user.getId(), user).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "update successful.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "update failed. Check the format.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("fail: ", t.getLocalizedMessage());
                    // Handle the error
                    Toast.makeText(getActivity(), "update failed. Server failure.", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        if(!ValidateEmail(newEmail))
            return;

        user.setEmail(newEmail);
        user.setPassword(newPassword);

        Retrofit retrofit = RetrofitService.getRetrofitInstance(getActivity());
        UserService userService = retrofit.create(UserService.class);

        userService.updateUser(user.getId(), user).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "update successful.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "update failed. Check the format.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(getActivity(), "update failed. Server failure.", Toast.LENGTH_LONG).show();
            }
        });



        // TODO validate mail - done
        // TODO check DB for same mail - done
        // TODO validate password requirements
        // TODO change data
        // TODO make toasts depending on result of attempt
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEdit = requireView().findViewById(R.id.editTextTextEmailAddress);
        passwordEdit = requireView().findViewById(R.id.editTextTextPassword);

        cancelButton = requireView().findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(listener -> {
            emailEdit.clearFocus();
            emailEdit.setText("");
            passwordEdit.clearFocus();
            passwordEdit.setText("");

            // TODO return to home fragment
        });

        submitButton = requireView().findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(listener -> {
            UserDtoResultLiveData.getInstance().observe(getViewLifecycleOwner(), userDTO -> {
                if (userDTO != null) {
                    // Handle user success
                    AttemptDataChange(userDTO);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    // Handle user failure
                    Log.d("error", "User not found.");
                }
            });
        });
    }

    private boolean ValidateEmail(String email){
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        if (email.matches(emailPattern)){
            return true;
        }

        Toast.makeText(getActivity(), "email format is wrong.", Toast.LENGTH_LONG).show();
        return false;
    }
}