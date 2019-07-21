package com.flamingo.wikiquiz;

import android.app.Application;

import java.util.List;

public class StatsRepository {

    private UserQuizStatDao _statsDao;
    private List<UserQuizStat> _allUserStats;

    StatsRepository(Application application) {
        InfoboxRoomDatabase db = InfoboxRoomDatabase.getDatabase(application);
        _statsDao = db.userQuizStatDao();
        _allUserStats = _statsDao.getAllUserStats();
    }

    List<UserQuizStat> getAllUserStats() {
        return _statsDao.getAllUserStats();
    }

    void insert(UserQuizStat userQuizStat) {
        _statsDao.insert(userQuizStat);
    }
}
