package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.List;

public class EndQuizFragment extends Fragment {
    private StatsViewModel statsViewModel;

    public EndQuizFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        statsViewModel = ViewModelProviders.of(getActivity()).get(StatsViewModel.class);

        // Inflate the layout for this fragment
        int score = getArguments().getInt("score");
        int numQuestions = getArguments().getInt("numQuestions");
        UserQuizStat userQuizStat = new UserQuizStat(numQuestions, score);
        statsViewModel.insertStat(userQuizStat);
        List<String> list = statsViewModel.getAllScoresStrings();

//        Log.e("EndQuizFragment", list.get(list.size()-1));
        View view = inflater.inflate(R.layout.fragment_end_quiz, container, false);
        TextView tv = view.findViewById(R.id.scoreDisplay);
        tv.setText("Final score: " + score);
        Button restartQuiz = view.findViewById(R.id.restartQuizButton);
        restartQuiz.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_endQuizFragment_to_launchQuizFragment, null));
        return view;
    }
}
