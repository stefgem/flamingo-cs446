package com.flamingo.wikiquiz;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;


public class LaunchQuizFragment extends Fragment {

    private QuestionViewModel questionViewModel;

    public LaunchQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);

        View view = inflater.inflate(R.layout.fragment_launch_quiz, container, false);

        Button quizBtn = view.findViewById(R.id.launchQuizButton);
        quizBtn.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_launchQuizFragment_to_questionFragment, null));

        Button multiBtn = view.findViewById(R.id.launchMultiplayerButton);
        multiBtn.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_launchQuizFragment_to_multiplayerFragment, null));

        Button loadBtn = view.findViewById(R.id.loadWikiDataButton);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionViewModel.fetchDataFromWikipedia();
                String toastMsg = "Database load initiated. \n Due to sync issues, the data may not display" +
                        " until the app activity is killed and relaunched.";
                Toast.makeText(getContext(),toastMsg,Toast.LENGTH_LONG ).show();
            }
        });

        return view;
    }

}
