package com.flamingo.wikiquiz;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserQuizStatDao {

    @Query("SELECT * FROM userQuizStat_table ORDER BY score DESC")
    List<UserQuizStat> getAllUserStats();

    @Insert
    void insert(UserQuizStat userQuizStat);
}
