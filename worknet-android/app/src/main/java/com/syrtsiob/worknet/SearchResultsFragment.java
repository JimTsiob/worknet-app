package com.syrtsiob.worknet;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {

    static final String DEFAULT_MODE = "default_mode";
    static final String HOME_FRAG_MODE = "home_frag_mode";

    private static final String RESULTS_MODE = "results_mode";
    private static final String SEARCH_INPUT = "search_input";

    private String resultsMode;
    private String searchInput;

    LinearLayout resultsContainer;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use to create a new instance of search results
     *
     * @param resultsMode The results mode depending on the context. Use SearchResultsFragment's static finals
     * @param searchInput The user's search input.
     * @return A new instance of fragment SearchResultsFragment.
     */
    public static SearchResultsFragment newInstance(String resultsMode, String searchInput) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(RESULTS_MODE, resultsMode);
        args.putString(SEARCH_INPUT, searchInput);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resultsMode = getArguments().getString(RESULTS_MODE);
            searchInput = getArguments().getString(SEARCH_INPUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultsContainer = requireView().findViewById(R.id.resultsContainer);

        switch (resultsMode) {
            case HOME_FRAG_MODE:
                HandleHomeFragMode(searchInput);
                break;
            case DEFAULT_MODE:
                HandleDefaultMode(searchInput);
                break;
            default:
                HandleUnknownMode();
        }
    }

    private void HandleDefaultMode(String searchInput) {
        // TODO implement
        // if (results != null)

        // else if (results == null)
        ShowNoResults();
    }

    private void HandleHomeFragMode(String searchInput) {
        // TODO implement
        // if (results != null)

        // else if (results == null)
        ShowNoResults();
    }

    private void HandleUnknownMode() {
        Toast.makeText(getActivity(),
                "Error when loading search results", Toast.LENGTH_LONG).show();
    }

    private void ShowNoResults() {
        TextView errorMessage = new TextView(getActivity());
        errorMessage.setText(R.string.no_results);
        errorMessage.setTextSize(20);
        errorMessage.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 64, 0, 0);
        errorMessage.setLayoutParams(params);
        errorMessage.setGravity(Gravity.CENTER);

        resultsContainer.addView(errorMessage);
    }
}