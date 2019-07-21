package com.flamingo.wikiquiz;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class StatsViewModel extends AndroidViewModel {
    private StatsRepository _repository;
    private List<UserQuizStat> _allScores;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        _repository = new StatsRepository(application);
        _allScores = _repository.getAllUserStats();
    }

    public List<String> getAllScoresStrings() {
        List<String> formattedStrings = new ArrayList<>();
        _allScores = _repository.getAllUserStats();
        Log.e("All scores", "" + _allScores);
        for (UserQuizStat userQuizStat: _allScores) {
            String tempString = "Questions: " + userQuizStat.getNumQuestions() + ", Score: " + userQuizStat.getScore();
            formattedStrings.add(tempString);
        }
        return formattedStrings;
    }

    public void insertStat(UserQuizStat userQuizStat) {
        _repository.insert(userQuizStat);
    }
}
