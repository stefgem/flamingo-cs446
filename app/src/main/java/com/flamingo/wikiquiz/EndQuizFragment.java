package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class EndQuizFragment extends Fragment {
    public EndQuizFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int score = getArguments().getInt("score");
        View view = inflater.inflate(R.layout.fragment_end_quiz, container, false);
        TextView tv = view.findViewById(R.id.scoreDisplay);
        tv.setText("Final score: " + score);
        return view;
    }
}
