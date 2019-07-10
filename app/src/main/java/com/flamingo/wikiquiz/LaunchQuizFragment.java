package com.flamingo.wikiquiz;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class LaunchQuizFragment extends Fragment {


    public LaunchQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_launch_quiz, container, false);

        Button button = view.findViewById(R.id.launchQuizButton);
        button.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_launchQuizFragment_to_questionFragment, null));

        return view;
    }

}
