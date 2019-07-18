package com.flamingo.wikiquiz;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class QuestionViewModel extends AndroidViewModel {

    private InfoboxRepository _repository;
    private LiveData<List<Infobox>> _allInfoBoxes;
    private Infobox[] _infoboxesInCategory;

    public QuestionViewModel(@NonNull Application application) {
        super(application);
        _repository = new InfoboxRepository(application);
        _allInfoBoxes = _repository.getAllInfoboxes();
    }

    LiveData<List<Infobox>> getAllInfoBoxes() {
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
}
