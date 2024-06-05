package com.syrtsiob.worknet;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.model.ConnectionDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NetworkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkFragment extends Fragment {

    LinearLayout networkList;

    ImageView profilePic;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NetworkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetworkFragment newInstance(String param1, String param2) {
        NetworkFragment fragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
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
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        networkList = requireView().findViewById(R.id.network_list);

        UserDtoResultLiveData.getInstance().observe(getActivity(), userDTO -> {
            List<ConnectionDTO> connections = userDTO.getConnections();
            if (connections.isEmpty()){
                TextView noConnectionsTextView = new TextView(getActivity());
                noConnectionsTextView.setText("You have no connections. \n");
                noConnectionsTextView.setTextSize(20); // Set desired text size
                noConnectionsTextView.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(300, 300, 16, 16);
                noConnectionsTextView.setLayoutParams(params);

                networkList.addView(noConnectionsTextView);

                TextView addConnectionsTextView = new TextView(getActivity());
                addConnectionsTextView.setText("Add some through the search bar!");
                addConnectionsTextView.setTextSize(20);
                noConnectionsTextView.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params2.setMargins(200, 5, 16, 16);
                addConnectionsTextView.setLayoutParams(params2);

                networkList.addView(addConnectionsTextView);
            }

            for (ConnectionDTO connection : connections){
                addEntryToList(connection);
            }
        });


    }

    private void addEntryToList(ConnectionDTO connection) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View networkListEntry = inflater
                .inflate(R.layout.network_list_entry_template, networkList, false);

        TextView fullName = networkListEntry.findViewById(R.id.full_name);
        TextView position = networkListEntry.findViewById(R.id.position);
        TextView employer = networkListEntry.findViewById(R.id.employer);

        fullName.setText(connection.getFirstName() + " " + connection.getLastName());
        List<WorkExperienceDTO> workExperiences = connection.getWorkExperiences();

        String positionText =  workExperiences.stream()
                .filter(WorkExperienceDTO::isCurrentlyWorking)
                .map(WorkExperienceDTO::getTitle)
                .findFirst()
                .orElse(null);

        position.setText(positionText);

        String employerText =  workExperiences.stream()
                .filter(WorkExperienceDTO::isCurrentlyWorking)
                .map(WorkExperienceDTO::getCompanyName)
                .findFirst()
                .orElse(null);

        employer.setText(employerText);

        Button goToProfileButton = networkListEntry.findViewById(R.id.goToProfileButton);

        goToProfileButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        networkList.addView(networkListEntry);
    }
}