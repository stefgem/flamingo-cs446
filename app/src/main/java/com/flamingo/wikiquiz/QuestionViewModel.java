package com.flamingo.wikiquiz;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class QuestionViewModel extends AndroidViewModel {

    private InfoboxRepository _repository;
    private List<Infobox> _allInfoBoxes;
    private List<Infobox> _infoboxesInCategory;
    private int numberOfQuestionTypes = 2;

    public QuestionViewModel(@NonNull Application application) {
        super(application);
        _repository = new InfoboxRepository(application);
        _allInfoBoxes = _repository.getAllInfoboxes();
    }

    List<Infobox> getAllInfoBoxes() {
        return _allInfoBoxes;
    }

    public LiveData<List<Infobox>> getInfoboxesInCategory(String category) {
        return _repository.getInfoboxesInCategory(category);
    }

    public void insert(Infobox infobox) {
        _repository.insert(infobox);
    }

    public void deleteAllDatabaseRows() {
        _repository.deleteAll();
    }

    @Nullable // if the id doesn't match an entry, this method will return null
    public Infobox getInfoboxById(int id) {

        return _repository.getInfoboxById(id);

    }

    public LiveData<List<Integer>> getInfoboxIdList() {
        return _repository.getInfoboxIdList();
    }


    public QuestionContent getQuestionContent() {

        int infoBoxSize = _allInfoBoxes.size();

        QuestionContent qc = new QuestionContent();
        int chooseQuestion = new Random().nextInt(numberOfQuestionTypes);


        if (infoBoxSize != 0) {
            if (chooseQuestion == 0) {
                int questionInfoboxIndex = new Random().nextInt(infoBoxSize);
                Infobox questionInfobox = _allInfoBoxes.get(questionInfoboxIndex);
                qc.imagePath = "";
                qc.questionString = "What is this person's name?";
                qc.answers = new ArrayList<String>(Collections.nCopies(4, ""));
                qc.correctAnswer = new Random().nextInt(4);
                qc.answers.set(qc.correctAnswer, questionInfobox.getName());
                ArrayList<Integer> otherAnswers = new ArrayList<>();
                otherAnswers.add(questionInfoboxIndex);
                for (int i = 0; i < 4; i++) {
                    if (i == qc.correctAnswer) {
                        continue;
                    }
                    while (qc.answers.get(i).equals("")) {
                        int otherAnswerIndex = new Random().nextInt(infoBoxSize);
                        if (otherAnswers.contains(otherAnswerIndex)) {
                            continue;
                        }
                        otherAnswers.add(otherAnswerIndex);
                        qc.answers.set(i, _allInfoBoxes.get(otherAnswerIndex).getName());
                    }
                }
            } else {
                int questionInfoboxIndex = new Random().nextInt(infoBoxSize);
                Infobox questionInfobox = _allInfoBoxes.get(questionInfoboxIndex);
                qc.imagePath = "";
                qc.questionString = "What year was this person born?";
                qc.answers = new ArrayList<String>(Collections.nCopies(4, ""));
                qc.correctAnswer = new Random().nextInt(4);
                qc.answers.set(qc.correctAnswer, Integer.toString(questionInfobox.getBirthYear()));
                ArrayList<String> otherAnswers = new ArrayList<>();
                otherAnswers.add(Integer.toString(questionInfobox.getBirthYear()));
                for (int i = 0; i < 4; i++) {
                    if (i == qc.correctAnswer) {
                        continue;
                    }
                    while (qc.answers.get(i).equals("")) {
                        int otherAnswerIndex = new Random().nextInt(infoBoxSize);
                        Infobox otherInfobox = _allInfoBoxes.get(otherAnswerIndex);
                        if (otherAnswers.contains(Integer.toString(otherInfobox.getBirthYear()))) {
                            continue;
                        }
                        otherAnswers.add(Integer.toString(otherInfobox.getBirthYear()));
                        qc.answers.set(i, Integer.toString(otherInfobox.getBirthYear()));
                    }
                }
            }
        } return qc;
    }
}
