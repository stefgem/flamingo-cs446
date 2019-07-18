package com.flamingo.wikiquiz;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class InfoboxRepository {

    private InfoboxDao _infoboxDao;
    private LiveData<List<Infobox>> _allInfoboxes;
    private Infobox _queriedInfobox;
    private LiveData<List<Integer>> _infoboxIdList;
    private Infobox[] _infoboxesInCategory;

    InfoboxRepository(Application application) {
        InfoboxRoomDatabase db = InfoboxRoomDatabase.getDatabase(application);
        _infoboxDao = db.infoboxDao();
        _allInfoboxes = _infoboxDao.getAllInfoboxes();
        _infoboxIdList = _infoboxDao.getInfoboxIdList();
    }

    LiveData<List<Infobox>> getAllInfoboxes() {
        return _allInfoboxes;
    }

    LiveData<List<Infobox>> getInfoboxesInCategory(String category) {
        return _infoboxDao.getInfoboxesInCategory(category);
    }

    public void insert(Infobox infobox) {
        new insertAsyncTask(_infoboxDao).execute(infobox);
    }

    public void deleteAll() {
        _infoboxDao.deleteAll();
    }

    @Nullable // Note that this method may return null!!
    public Infobox getInfoboxById(int id) {

      return _infoboxDao.getInfoboxById(id);
    }

    public LiveData<List<Integer>> getInfoboxIdList() {
        return _infoboxIdList;
    }

    private static class insertAsyncTask extends AsyncTask<Infobox, Void, Void> {
        private InfoboxDao _AsyncTaskDao;

        insertAsyncTask(InfoboxDao dao) {
            _AsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Infobox... params) {
            _AsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    /* private static class getInfoboxByIdAsyncTask extends AsyncTask<Integer, Void, LiveData<Infobox>> {
        private InfoboxDao _AsyncTaskDao;

        getInfoboxByIdAsyncTask(InfoboxDao dao) {
            _AsyncTaskDao = dao;
        }
        @Override
        protected LiveData<Infobox> doInBackground(final Integer... params) {
            return _AsyncTaskDao.getInfoboxById(params[0]);
        }
    } */
}
