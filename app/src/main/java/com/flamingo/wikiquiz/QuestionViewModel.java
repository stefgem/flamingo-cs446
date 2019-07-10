package com.flamingo.wikiquiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class QuestionViewModel extends AndroidViewModel {

    private InfoboxRepository _repository;
    private LiveData<List<Infobox>> _allInfoBoxes;

    public QuestionViewModel(@NonNull Application application) {
        super(application);
        _repository = new InfoboxRepository(application);
        _allInfoBoxes = _repository.getAllInfoboxes();
    }

    LiveData<List<Infobox>> getAllInfoBoxes() {
        return _allInfoBoxes;
    }

    public void insert(Infobox infobox) {
        _repository.insert(infobox);
    }
}
