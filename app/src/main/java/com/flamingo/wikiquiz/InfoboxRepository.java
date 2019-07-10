package com.flamingo.wikiquiz;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class InfoboxRepository {

    private InfoboxDao _infoboxDao;
    private LiveData<List<Infobox>> _allInfoboxes;

    InfoboxRepository(Application application) {
        InfoboxRoomDatabase db = InfoboxRoomDatabase.getDatabase(application);
        _infoboxDao = db.infoboxDao();
        _allInfoboxes = _infoboxDao.getAllInfoboxes();
    }

    LiveData<List<Infobox>> getAllInfoboxes() {
        return _allInfoboxes;
    }

    public void insert(Infobox infobox) {
        new insertAsyncTask(_infoboxDao).execute(infobox);
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
}
